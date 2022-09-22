package com.feelmycode.parabole.controller;

import com.feelmycode.parabole.dto.EventApplyDto;
import com.feelmycode.parabole.service.EventParticipantService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/event")
@RequiredArgsConstructor
public class EventApplyController {

    private final EventParticipantService eventParticipantService;

    @ExceptionHandler(value=Exception.class)
    @RequestMapping(value = "/participant",method = RequestMethod.POST)
    public ResponseEntity<String> insertFcFsApply(@RequestBody @Valid EventApplyDto dto){

        boolean result=eventParticipantService.eventJoin(dto);
        if(result) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("응모가 완료 되었습니다");
        }

        return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body("이미 응모가 완료 되었습니다");
    }
}
