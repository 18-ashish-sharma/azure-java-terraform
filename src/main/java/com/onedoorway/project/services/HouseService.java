package com.onedoorway.project.services;

import com.onedoorway.project.Context;
import com.onedoorway.project.dto.*;
import com.onedoorway.project.exception.HouseServiceException;
import com.onedoorway.project.model.*;
import com.onedoorway.project.repository.ClientRepository;
import com.onedoorway.project.repository.HouseContactRepository;
import com.onedoorway.project.repository.HouseRepository;
import com.onedoorway.project.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@Transactional
public class HouseService {
    private final HouseRepository houseRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final HouseContactRepository houseContactRepository;
    private final Context context;

    @Autowired
    public HouseService(
            HouseRepository houseRepository,
            ClientRepository clientRepository,
            UserRepository userRepository,
            HouseContactRepository houseContactRepository,
            Context context) {
        this.houseRepository = houseRepository;
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
        this.houseContactRepository = houseContactRepository;
        this.context = context;
    }

    @PreAuthorize("@context.isAdmin()")
    public void createHouse(AddHouseRequest request) throws HouseServiceException {
        House house = houseRepository.getByHouseCode(request.getHouseCode());
        if (house == null) {
            house =
                    House.builder()
                            .houseCode(request.getHouseCode())
                            .phone(request.getPhone())
                            .addrLine1(request.getAddrLine1())
                            .addrLine2(request.getAddrLine2())
                            .city(request.getCity())
                            .state(request.getState())
                            .deleted(false)
                            .postCode(request.getPostCode())
                            .build();
            houseRepository.save(house);
            log.info("Created the house with houseCode {}", request.getHouseCode());
        } else {
            log.error("House with the same houseCode {} already exists.", request.getHouseCode());
            throw new HouseServiceException("Duplicate house creation with same houseCode");
        }
    }

    public HouseDTO getHouseByCode(GetHouseByCodeRequest request) throws HouseServiceException {
        House house = getHouse(request.getHouseCode());
        log.info("Fetched the house with houseCode {}", request.getHouseCode());
        return new ModelMapper().map(house, HouseDTO.class);
    }

    private House getHouse(String houseCode) throws HouseServiceException {
        House house = houseRepository.getByHouseCode(houseCode);
        if (house == null) {
            String errorMessage =
                    String.format("Cannot get a house for the house code %s", houseCode);
            log.error(errorMessage);
            throw new HouseServiceException(errorMessage);
        }
        return house;
    }

    public List<ClientDTO> getClientsByHouse(GetClientsByHouseRequest request) {
        List<ClientDTO> res =
                clientRepository.findByHouse_HouseCode(request.getHouseCode()).stream()
                        .map(item -> new ModelMapper().map(item, ClientDTO.class))
                        .collect(Collectors.toList());
        log.info("Fetched the clients with houseCode {}", request.getHouseCode());
        return res;
    }

    public void addClient(Long clientId, String houseCode) throws HouseServiceException {
        House house = getHouse(houseCode);
        Optional<Client> client = clientRepository.findById(clientId);
        if (client.isPresent()) {
            Client clientToUpdate = client.get();
            clientToUpdate.setHouse(house);
            clientRepository.save(clientToUpdate);
            log.info(
                    "Mapped the client with clientId {} and to the house with houseCode {}",
                    clientId,
                    houseCode);
        } else {
            log.error("Error in mapping clientId {} and houseCode {}", clientId, houseCode);
            throw new HouseServiceException("Invalid data sent to map client to a house.");
        }
    }

    @PreAuthorize("@context.isAdmin()")
    public void mapHouseToUser(long userId, String houseCode) throws HouseServiceException {
        House house = getHouse(houseCode);
        User user = userRepository.getById(userId);
        if (user != null) {
            user.getHouses().add(house);
            userRepository.save(user);
            log.info(
                    "Mapped the user with userId {} and to the house with houseCode {}",
                    userId,
                    houseCode);
        } else {
            log.error("Error in mapping userId {} and houseCode {}", userId, houseCode);
            throw new HouseServiceException("Invalid data sent to map user to a house.");
        }
    }

    public boolean isUserInHouse(String userName, String houseCode) throws HouseServiceException {
        House house = getHouse(houseCode);
        User user = userRepository.getByEmail(userName);
        log.info(
                "Checking for mapping for the user with userName {} and with houseCode {}",
                userName,
                houseCode);
        return user.getHouses().contains(house);
    }

    public void removeClient(String houseCode, long clientId) throws HouseServiceException {
        House house = getHouse(houseCode);
        Client client = clientRepository.getById(clientId);
        house.getClients().remove(client);
        houseRepository.save(house);
        client.setHouse(null);
        clientRepository.save(client);
        log.info("Removed the client {} from house {}", clientId, houseCode);
    }

    public List<HouseDTO> listAllHouses() {
        List<HouseDTO> houses =
                houseRepository.findAllByDeleted(false).stream()
                        .map(item -> new ModelMapper().map(item, HouseDTO.class))
                        .collect(Collectors.toList());

        Long userCount;
        Long clients;
        for (HouseDTO house : houses) {
            log.info(String.valueOf(house));
            userCount =
                    userRepository.countByHouses_HouseCodeAndDeleted(house.getHouseCode(), false);
            house.setTotalUsers(userCount);
            log.info(String.valueOf(userCount));
            clients =
                    clientRepository.countByHouse_HouseCodeAndDeleted(house.getHouseCode(), false);
            house.setTotalClients(clients);
        }

        return houses;
    }

    @PreAuthorize("@context.isAdmin()")
    public void updateHouse(AddHouseRequest request, long id) throws HouseServiceException {
        Optional<House> house = houseRepository.findById(id);
        if (house.isPresent()) {
            House newHouse = house.get();
            newHouse.setPhone(request.getPhone());
            newHouse.setAddrLine1(request.getAddrLine1());
            newHouse.setAddrLine2(request.getAddrLine2());
            newHouse.setCity(request.getCity());
            newHouse.setState(request.getState());
            newHouse.setHouseCode(request.getHouseCode());
            newHouse.setPostCode(request.getPostCode());
            newHouse.setDeleted(request.getDeleted());
            houseRepository.save(newHouse);
            log.info("Updated the house with the id {}", id);
        } else {
            log.info("house with id {} not found", id);
            throw new HouseServiceException("house with id {} not found");
        }
    }

    public void createHouseContact(HouseContactRequest request) throws HouseServiceException {
        String email = context.currentUser();
        User user = userRepository.getByEmail(email);
        if (user == null) {
            handleError("Unauthenticated session or no user found for email %s", email);
        }
        log.info("Creating houseContact for house with houseCode {}", request.getHouseCode());

        Set<House> houseSet =
                request.getHouseCode().stream()
                        .map(houseRepository::getByHouseCode)
                        .collect(Collectors.toSet());
        log.info("houseSet {}", houseSet);
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(
                new PropertyMap<HouseContactRequest, HouseContact>() {
                    @Override
                    protected void configure() {
                        skip(destination.getHouses());
                    }
                });
        HouseContact houseContact = modelMapper.map(request, HouseContact.class);
        houseContact.setHouses(houseSet);
        houseContact.setStatus(NoticeStatus.ACTIVE);
        houseContact.setLastUpdatedAt(context.now());
        houseContactRepository.save(houseContact);
        log.info("Created the houseContact for house with houseCode {}", houseContact.getId());
    }

    public HouseContactDTO getHouseContactById(long id) throws HouseServiceException {
        Optional<HouseContact> houseContact = houseContactRepository.findById(id);
        if (houseContact.isEmpty()) {
            throw new HouseServiceException("No houseContact found with id " + id);
        }
        log.info("Fetched the HouseContacts based on id {}", id);
        return new ModelMapper().map(houseContact.get(), HouseContactDTO.class);
    }

    public void updateHouseContact(Long id, UpdateHouseContactRequest request)
            throws HouseServiceException {
        List<HouseContact> houseContacts =
                houseContactRepository.findAllByHouses_HouseCodeIn(request.getHouseCode());
        if (houseContacts.isEmpty()) {
            throw new HouseServiceException(
                    "No houseContact found with houseCode" + request.getHouseCode());
        }
        Optional<HouseContact> houseContact = houseContactRepository.findById(id);
        if (houseContact.isPresent()) {
            HouseContact existingHouseContact = houseContact.get();
            ModelMapper mapper = new ModelMapper();
            mapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
            mapper.addMappings(
                    new PropertyMap<UpdateHouseContactRequest, HouseContact>() {
                        @Override
                        protected void configure() {
                            skip(destination.getHouses());
                        }
                    });
            mapper.map(request, existingHouseContact);
            if (StringUtils.hasText(request.getStatus())) {
                existingHouseContact.setStatus(NoticeStatus.valueOf(request.getStatus()));
            }
            Set<House> houseSet =
                    request.getHouseCode().stream()
                            .map(houseRepository::getByHouseCode)
                            .collect(Collectors.toSet());
            existingHouseContact.setHouses(houseSet);
            existingHouseContact.setLastUpdatedAt(context.now());
            houseContactRepository.save(existingHouseContact);
            log.info("Updated the houseContact for the given id {}", id);
        } else {
            String errorMessage = String.format("Cannot get a houseContact for the id %d", id);
            log.error(errorMessage);
            throw new HouseServiceException(errorMessage);
        }
    }

    public List<HouseContactDTO> listHouseContacts(ListHouseContactRequest request) {
        List<HouseContact> res;
        Pageable page =
                PageRequest.of(
                        request.getPageNumber(), request.getPageSize(), Sort.by("id").descending());
        log.info("Fetching house contacts based on request");
        if (request.getHouseCode() != null) {
            log.info("Fetching the house contacts based on houseCode");
            res = houseContactRepository.findAllByHouses_HouseCode(page, request.getHouseCode());
        } else {
            log.info("Fetching all the house contacts");
            res = houseContactRepository.findAll(page).getContent();
        }
        log.info("Fetched the houseContacts with houseCode {}", request.getHouseCode());
        return res.stream()
                .map(item -> new ModelMapper().map(item, HouseContactDTO.class))
                .collect(Collectors.toList());
    }

    private void handleError(String message, String... params) throws HouseServiceException {
        String errorMessage = String.format(message, params);
        log.error(errorMessage);
        throw new HouseServiceException(errorMessage);
    }
}
