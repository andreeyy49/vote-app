package voteapp.geostorageservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import voteapp.geostorageservice.dto.LoginDto;
import voteapp.geostorageservice.dto.UserLoginDto;

@FeignClient(name = "authClient", url = "${external-api.authServiceUrl}")
public interface AuthClient {

    @GetMapping(value = "/validate-token")
    boolean validateToken(@RequestHeader(value = "authorization") String authorizationHeader);

    @GetMapping(value = "/login")
    LoginDto login(@RequestBody UserLoginDto userLoginDto);
}
