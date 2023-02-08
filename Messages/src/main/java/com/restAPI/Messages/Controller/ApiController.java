package com.restAPI.Messages.Controller;

import com.restAPI.Messages.Models.Message;
import com.restAPI.Messages.Repo.MessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
public class ApiController {

    @Autowired
    private MessageRepo repo;

    @GetMapping(value = "/")
    public ResponseEntity<String> getPage() {
        return new ResponseEntity<>("Welcome", HttpStatus.OK);
    }

    @GetMapping(value = "/messages")
    public ResponseEntity<List<Message>> getAllMessages(
            @RequestParam(value = "start", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = Integer.MAX_VALUE+"") int limit
    ) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Message> messages = repo.findAll(pageable);
        if (messages.hasContent()) {
            return new ResponseEntity<>(messages.getContent(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping(value = "/messages/{id}")
    public ResponseEntity<Message> getMessageById(@PathVariable("id") long id) {
        Optional<Message> message = repo.findById(id);
        if (message.isPresent()) {
            return new ResponseEntity<>(message.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/save/{message}" , method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseEntity<String> createMessage(@PathVariable("message") String message,
                                                @RequestParam(value = "author", required = false, defaultValue = "Anonymous") String author,
                                                @RequestParam(value = "date", required = false)
                                                @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<LocalDate> dateCreated) {
        Message storeMessage = new Message();
        storeMessage.setMessage(message);
        storeMessage.setAuthor(author);
        storeMessage.setDateCreated(dateCreated.orElse(LocalDate.now()));
        repo.save(storeMessage);
        return new ResponseEntity<>("Saved Message Successfully", HttpStatus.CREATED);
    }

    @RequestMapping(value = "/update/{id}",method = {RequestMethod.GET,RequestMethod.PUT})
    public ResponseEntity<String> updateMessage(@PathVariable("id") long id,
                                                @RequestParam("message") String message,
                                                @RequestParam(value = "author", required = false, defaultValue = "Anonymous") String author,
                                                @RequestParam(value = "date", required = false)
                                                @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<LocalDate> dateCreated) {
        Optional<Message> existingMessage = repo.findById(id);
        if (existingMessage.isPresent()) {
            Message updatedMessage = existingMessage.get();
            updatedMessage.setMessage(message);
            updatedMessage.setAuthor(author);
            updatedMessage.setDateCreated(dateCreated.orElse(LocalDate.now()));
            repo.save(updatedMessage);
            return new ResponseEntity<>("Message Updated Successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/delete/{id}" , method = {RequestMethod.GET,RequestMethod.DELETE})
    public ResponseEntity<String> deleteMessage(@PathVariable(value = "id") long id) {
        Optional<Message> message = repo.findById(id);
        if (message.isPresent()) {
            repo.delete(message.get());
            return new ResponseEntity<>("Record deleted", HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}
