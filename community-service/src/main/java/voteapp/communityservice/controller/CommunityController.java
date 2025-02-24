package voteapp.communityservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/findAllByIds")
    public List<Community> findAllByIds(@RequestBody List<Long> ids) {
        return communityService.findAllById(ids);
    }

    @PostMapping
    public Community save(@RequestBody Community community) {
        return communityService.save(community);
    }

    @PutMapping
    public Community update(@RequestBody Community community) {
        return communityService.update(community);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        communityService.deleteById(id);
    }
}
