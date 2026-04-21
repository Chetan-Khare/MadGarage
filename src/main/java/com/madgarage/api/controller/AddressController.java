package com.madgarage.api.controller;

import com.madgarage.api.model.User;
import com.madgarage.api.model.UserAddress;
import com.madgarage.api.services.AddressService;
import com.madgarage.api.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserAddress>> getMyAddresses(Principal principal) {
        User user = userService.getCurrentUser(principal.getName());
        return ResponseEntity.ok(addressService.getAddressesByUserId(user.getId()));
    }

    @PostMapping
    public ResponseEntity<UserAddress> addAddress(Principal principal, @RequestBody UserAddress address) {
        User user = userService.getCurrentUser(principal.getName());
        return ResponseEntity.ok(addressService.addAddress(user, address));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserAddress> updateAddress(Principal principal, @PathVariable Long id, @RequestBody UserAddress address) {
        User user = userService.getCurrentUser(principal.getName());
        return ResponseEntity.ok(addressService.updateAddress(user.getId(), id, address));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAddress(Principal principal, @PathVariable Long id) {
        User user = userService.getCurrentUser(principal.getName());
        addressService.deleteAddress(user.getId(), id);
        return ResponseEntity.ok().build();
    }
}
