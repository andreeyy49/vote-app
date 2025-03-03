package voteapp.communityservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import voteapp.communityservice.dto.ModeratorRequest;
import voteapp.communityservice.model.Community;
import voteapp.communityservice.service.CommunityService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/community")
public class CommunityController {

    private final CommunityService communityService;

    @GetMapping
    public List<Community> findAll() {
        return communityService.findAll();
    }

    @GetMapping("/{id}")
    public Community findById(@PathVariable Long id) {
        return communityService.findById(id);
    }

    @GetMapping("findAllByFragment/{fragment}")
    public List<Community> findAllByFragment(@PathVariable String fragment) {
        return communityService.findByTitleFragment(fragment);
    }

    @PostMapping("/findAllByIds")
    public List<Community> findAllByIds(@RequestBody List<Long> ids) {
        return communityService.findAllById(ids);
    }

    @GetMapping("/findByTitle/{title}")
    public Boolean findByTitle(@PathVariable String title) {
        return communityService.findByTitle(title);
    }

    @PostMapping
    public Community save(@RequestBody Community community) {
        return communityService.save(community);
    }

    @PutMapping
    public Community update(@RequestBody Community community) {
        return communityService.update(community);
    }

    @GetMapping("/isAdmin")
    public Boolean isAdmin() {
        return communityService.isAdmin();
    }

    @GetMapping("/isModerator")
    public Boolean isModerator() {
        return communityService.isModerator();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        communityService.deleteById(id);
    }

    @GetMapping("/findAllByModerator")
    public List<Community> findAllByModerator() {
        return communityService.findAllByModeratorsContains();
    }

    @GetMapping("/findAllByAdmin")
    public List<Community> findAllByAdmin() {
        return communityService.findAllByAdmin();
    }

    @PostMapping("/createModerator")
    public void createModerator(@RequestBody ModeratorRequest moderatorRequest) {
        communityService.createModerator(moderatorRequest);
    }

    @DeleteMapping("/removeModerator")
    public void removeModerator(@RequestBody ModeratorRequest moderatorRequest) {
        communityService.removeModerator(moderatorRequest);
    }
}
