package com.onedoorway.project.services;

import com.onedoorway.project.Context;
import com.onedoorway.project.dto.*;
import com.onedoorway.project.exception.UserServiceException;
import com.onedoorway.project.model.House;
import com.onedoorway.project.model.PasswordResetToken;
import com.onedoorway.project.model.Role;
import com.onedoorway.project.model.User;
import com.onedoorway.project.repository.HouseRepository;
import com.onedoorway.project.repository.PasswordTokenRepository;
import com.onedoorway.project.repository.RoleRepository;
import com.onedoorway.project.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final HouseRepository houseRepository;
    private final PasswordTokenRepository passwordTokenRepository;
    private final EmailSenderService emailSenderService;
    private final Context context;
    private final String url;

    @Autowired
    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            HouseRepository houseRepository,
            PasswordTokenRepository passwordTokenRepository,
            EmailSenderService emailSenderService,
            @Value("${spring.sendgrid.url}") String url,
            Context context) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.houseRepository = houseRepository;
        this.passwordTokenRepository = passwordTokenRepository;
        this.emailSenderService = emailSenderService;
        this.url = url;
        this.context = context;
    }

    @PreAuthorize("@context.isAdmin()")
    @SneakyThrows
    public void register(
            String email,
            String password,
            String firstName,
            String lastName,
            String phone,
            String mobile)
            throws UserServiceException {

        if (userRepository.findByEmailIgnoreCase(email) != null) {
            throw new UserServiceException("User with same email already exists");
        }

        Role role = roleRepository.getByName("USER");

        if (role == null) {
            throw new UserServiceException("Role not found for creating the user");
        }

        userRepository.save(
                User.builder()
                        .email(email)
                        .firstName(firstName)
                        .lastName(lastName)
                        .deleted(false)
                        .phone(phone)
                        .mobile(mobile)
                        .password(passwordEncoder.encode(password))
                        .roles(Set.of(role))
                        .build());

        log.info("Created the user with email {}", email);
    }

    public List<UserShortDTO> listAllUsers() {
        ModelMapper modelMapper = new ModelMapper();
        TypeMap<User, UserShortDTO> typeMap =
                modelMapper.createTypeMap(User.class, UserShortDTO.class);
        typeMap.addMappings(
                mapper ->
                        mapper.using(new RoleListConverter())
                                .map(User::getRoles, UserShortDTO::setRoleNames));

        List<UserShortDTO> users =
                userRepository.findAllByDeleted(false, Pageable.unpaged()).stream()
                        .map(item -> modelMapper.map(item, UserShortDTO.class))
                        .collect(Collectors.toList());
        log.info("Fetched all users - count {}", users.size());
        return users;
    }

    public List<UserDTO> listAllUsersByPage(ListUsersByPageRequest request) {
        List<User> res;
        Pageable page =
                PageRequest.of(
                        request.getPageNumber(), request.getPageSize(), Sort.by("id").ascending());
        ModelMapper modelMapper = new ModelMapper();
        TypeMap<User, UserDTO> typeMap = modelMapper.createTypeMap(User.class, UserDTO.class);
        typeMap.addMappings(
                mapper ->
                        mapper.using(new RoleListConverter())
                                .map(User::getRoles, UserDTO::setRoleNames));
        typeMap.addMappings(
                mapper ->
                        mapper.using(new HouseCodeConverter())
                                .map(User::getHouses, UserDTO::setHouses));

        if (request.getNameOrEmail() != null && request.getHouseCode() == null) {
            log.info("Fetching the users based on the firstName , lastName ,Email and Deleted ");
            res =
                    userRepository
                            .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndDeleted(
                                    request.getNameOrEmail(),
                                    request.getNameOrEmail(),
                                    request.getNameOrEmail(),
                                    false,
                                    page);
        } else if (request.getNameOrEmail() == null && request.getHouseCode() != null) {
            log.info("Fetching the users based on houseCode and deleted");
            res =
                    userRepository.findByHouses_HouseCodeContainingIgnoreCaseAndDeleted(
                            request.getHouseCode(), false, page);
        } else if (request.getNameOrEmail() != null && request.getHouseCode() != null) {
            log.info(
                    "Fetching the users based on the firstName ,lastName ,Email ,HouseCode and Deleted ");
            res =
                    userRepository
                            .findByFirstNameContainingIgnoreCaseAndHouses_HouseCodeContainingIgnoreCaseAndDeletedOrLastNameContainingIgnoreCaseAndHouses_HouseCodeContainingIgnoreCaseAndDeletedOrEmailContainingIgnoreCaseAndHouses_HouseCodeContainingIgnoreCaseAndDeleted(
                                    request.getNameOrEmail(),
                                    request.getHouseCode(),
                                    false,
                                    request.getNameOrEmail(),
                                    request.getHouseCode(),
                                    false,
                                    request.getNameOrEmail(),
                                    request.getHouseCode(),
                                    false,
                                    page);

        } else {
            res = userRepository.findAllByDeleted(false, page);
        }
        log.info("Fetched {} users", res.size());
        return res.stream()
                .map(item -> modelMapper.map(item, UserDTO.class))
                .collect(Collectors.toList());
    }

    public Long usersCount(ListUsersByPageRequest request) {
        Long userCount;
        if (request.getNameOrEmail() != null && request.getHouseCode() == null) {
            log.info("Fetching the count based on the firstName , lastName ,Email and Deleted ");
            userCount =
                    userRepository
                            .countByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndDeleted(
                                    request.getNameOrEmail(),
                                    request.getNameOrEmail(),
                                    request.getNameOrEmail(),
                                    false);
        } else if (request.getNameOrEmail() == null && request.getHouseCode() != null) {
            log.info("Fetching the users based on houseCode and deleted");
            userCount =
                    userRepository.countByHouses_HouseCodeContainingIgnoreCaseAndDeleted(
                            request.getHouseCode(), false);
        } else if (request.getNameOrEmail() != null && request.getHouseCode() != null) {
            log.info(
                    "Fetching the users based on the firstName ,lastName ,Email ,HouseCode and Deleted ");
            userCount =
                    userRepository
                            .countByFirstNameContainingIgnoreCaseAndHouses_HouseCodeContainingIgnoreCaseAndDeletedOrLastNameContainingIgnoreCaseAndHouses_HouseCodeContainingIgnoreCaseAndDeletedOrEmailContainingIgnoreCaseAndHouses_HouseCodeContainingIgnoreCaseAndDeleted(
                                    request.getNameOrEmail(),
                                    request.getHouseCode(),
                                    false,
                                    request.getNameOrEmail(),
                                    request.getHouseCode(),
                                    false,
                                    request.getNameOrEmail(),
                                    request.getHouseCode(),
                                    false);
        } else {
            userCount = userRepository.countByDeleted(false);
        }
        log.info("Fetched all users - count {}", userCount);
        return userCount;
    }

    public List<UserShortDTO> listUsersByHouseCode(ListUserRequest request) {
        ModelMapper modelMapper = new ModelMapper();
        TypeMap<User, UserShortDTO> typeMap =
                modelMapper.createTypeMap(User.class, UserShortDTO.class);
        typeMap.addMappings(
                mapper ->
                        mapper.using(new RoleListConverter())
                                .map(User::getRoles, UserShortDTO::setRoleNames));
        List<UserShortDTO> users =
                userRepository
                        .findByHouses_HouseCodeAndDeleted(
                                request.getHouseCode(), false, Pageable.unpaged())
                        .stream()
                        .map(item -> modelMapper.map(item, UserShortDTO.class))
                        .collect(Collectors.toList());
        log.info("Fetched the users with houseCode {}", request.getHouseCode());
        return users;
    }

    @PreAuthorize("@context.isAdmin()")
    @Transactional
    public void removeRoleFromUser(long userId, long roleId) throws UserServiceException {
        Optional<Role> role = roleRepository.findById(roleId);
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent() && role.isPresent()) {
            User userEntity = user.get();
            Role roleEntity = role.get();
            userEntity.getRoles().remove(roleEntity);
            userRepository.save(userEntity);
            log.info(
                    "Removed the role {} from user {}",
                    roleEntity.getName(),
                    userEntity.getEmail());
        } else {
            log.error("Error in removing role {} from user {}", roleId, userId);
            throw new UserServiceException("Invalid data sent to remove role from a user.");
        }
    }

    @PreAuthorize("@context.isAdmin()")
    public void addRoleToUser(long userId, long roleId) throws UserServiceException {
        Optional<Role> role = roleRepository.findById(roleId);
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent() && role.isPresent()) {
            User userEntity = user.get();
            Role roleEntity = role.get();
            userEntity.getRoles().add(roleEntity);
            userRepository.save(userEntity);
            log.info(
                    "Added the role {} to the user {}",
                    roleEntity.getName(),
                    userEntity.getEmail());
        } else {
            log.error("Error in adding userId {} and roleId {}", userId, roleId);
            throw new UserServiceException("Invalid data sent to add role to a user.");
        }
    }

    @PreAuthorize("@context.isAdmin()")
    public void removeHouseFromUser(long userId, String houseCode) throws UserServiceException {
        Optional<User> user = userRepository.findById(userId);
        House house = houseRepository.getByHouseCode(houseCode);
        if (user.isPresent() && house != null) {
            User userEntity = user.get();
            userEntity.getHouses().remove(house);
            userRepository.save(userEntity);
            log.info("Removed the House{} from user {}", userId, houseCode);
        } else {
            log.error("Error removing user {} from house {}", userId, houseCode);
            throw new UserServiceException("Error removing user from house");
        }
    }

    public void updatePassword(ChangePasswordRequest request) throws UserServiceException {
        User user = userRepository.getByEmail(context.currentUser());
        if (user != null) {
            if (!request.getOldPassword().equals(request.getNewPassword())
                    && passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                userRepository.save(user);
                log.info("Updated the user for the given password ");
            } else {
                throw new UserServiceException("Incorrect Old Password");
            }
        } else {
            throw new UserServiceException("User not found");
        }
    }

    @Transactional
    @SneakyThrows
    public void forgotPassword(String email) throws UserServiceException {
        log.info("Requested password reset for email {}", email);
        User user = userRepository.getByEmail(email);
        if (user == null) {
            log.error("user not found for email {}", email);
            throw new UserServiceException("user not found");
        }
        LocalDateTime dateTime = LocalDateTime.now().plusDays(1L);
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken =
                PasswordResetToken.builder().user(user).token(token).expiryDate(dateTime).build();
        passwordTokenRepository.save(resetToken);
        log.info("Token generated for password reset with id {}", resetToken.getId());
        emailSenderService.sendEmail("OneDoorway Password Reset", buildContent(token), email);
    }

    private String buildContent(String token) {
        String newLine = System.getProperty("line.separator");
        return "Hi,"
                + newLine
                + "We have received a request to reset your password."
                + newLine
                + "Please click on the following link or paste this into your browser to proceed"
                + newLine
                + newLine
                + url
                + token
                + newLine
                + newLine
                + "If you did not request this please ignore this email, your password will remain unchanged."
                + newLine
                + "Thanks,"
                + "OneDoorWay Team";
    }

    public void resetPassword(ResetPasswordRequest request) throws UserServiceException {
        Optional<PasswordResetToken> passwordResetToken =
                passwordTokenRepository.getByToken(request.getToken());
        if (passwordResetToken.isEmpty()) {
            log.error("PasswordResetToken is not valid");
            throw new UserServiceException("PasswordResetToken not found");
        }
        LocalDateTime dateTime = passwordResetToken.get().getExpiryDate();
        LocalDateTime localDateTime = LocalDateTime.now();
        if (!localDateTime.isBefore(dateTime)) {
            log.error("PasswordResetToken expired");
            throw new UserServiceException("PasswordResetToken is expired");
        }
        if (!request.getConfirmPassword().equals(request.getNewPassword())) {
            log.error("Confirm password and new password are not matching");
            throw new UserServiceException("Confirm password and new Password not matches");
        }
        long id = passwordResetToken.get().getUser().getId();
        User user = userRepository.getById(id);
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Reset password is success");
    }

    @PreAuthorize("@context.isAdmin()")
    public void updateUser(long id, UpdateUserRequest request) throws UserServiceException {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            User existingEntity = user.get();
            existingEntity.setDeleted(request.getDeleted());
            existingEntity.setEmail(request.getEmail());
            existingEntity.setFirstName(request.getFirstName());
            existingEntity.setLastName(request.getLastName());
            existingEntity.setPhone(request.getPhone());
            existingEntity.setMobile(request.getMobile());
            userRepository.save(existingEntity);
            log.info("updated the user for the given id {}", id);
        } else {
            String errorMessage = String.format("Cannot find a user for the id %d", id);
            log.error(errorMessage);
            throw new UserServiceException(errorMessage);
        }
    }

    public GetUserDTO getUserById(long userId) throws UserServiceException {
        Optional<User> res = userRepository.findById(userId);
        if (res.isEmpty()) {
            throw new UserServiceException("No user found with userId " + userId);
        }
        log.info("Fetched the user based on userId {}", userId);
        return new ModelMapper().map(res.get(), GetUserDTO.class);
    }

    public boolean isUserDeleted(String email) throws UserServiceException {
        log.info("Checking if user is deleted with email {}", email);
        User user = userRepository.getByEmail(email);
        if (user == null) {
            return true;
        }
        if (user.getDeleted() == null) return false;
        else return user.getDeleted();
    }
}
