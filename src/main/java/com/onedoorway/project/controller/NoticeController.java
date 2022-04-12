package com.onedoorway.project.controller;

import com.onedoorway.project.dto.ListNoticeRequest;
import com.onedoorway.project.dto.NoticeDTO;
import com.onedoorway.project.dto.NoticeRequest;
import com.onedoorway.project.dto.Response;
import com.onedoorway.project.exception.NoticeServiceException;
import com.onedoorway.project.services.NoticeService;
import java.util.List;
import javax.validation.Valid;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/notice", produces = "application/json")
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(@Autowired NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @SneakyThrows
    @GetMapping("/get/{id}")
    public ResponseEntity<NoticeDTO> getNoticeById(@PathVariable long id) {
        return new ResponseEntity<>(noticeService.getNoticeById(id), HttpStatus.OK);
    }

    @SneakyThrows
    @PostMapping("/create")
    public ResponseEntity<Response> createNotice(@Valid @RequestBody NoticeRequest request) {
        noticeService.createNotice(request);
        return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Response> updateNotice(
            @PathVariable Long id, @Valid @RequestBody NoticeRequest request) {
        try {
            noticeService.updateNotice(id, request);
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (NoticeServiceException e) {
            return new ResponseEntity<>(
                    Response.builder().success(false).build(), HttpStatus.BAD_REQUEST);
        }
    }

    @SneakyThrows
    @PostMapping("/list")
    public ResponseEntity<List<NoticeDTO>> getNotices(@RequestBody ListNoticeRequest request) {
        return new ResponseEntity<>(noticeService.getNotices(request), HttpStatus.OK);
    }
}
