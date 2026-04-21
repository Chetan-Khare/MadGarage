package com.madgarage.api.services;

import com.madgarage.api.model.User;
import com.madgarage.api.model.UserAddress;
import com.madgarage.api.repository.UserAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final UserAddressRepository addressRepository;

    public List<UserAddress> getAddressesByUserId(Long userId) {
        return addressRepository.findByUserId(userId);
    }

    @Transactional
    public UserAddress addAddress(User user, UserAddress address) {
        if (Boolean.TRUE.equals(address.getIsDefault())) {
            resetDefaultAddresses(user.getId());
        }
        
        // If it's the first address, make it default
        List<UserAddress> existing = addressRepository.findByUserId(user.getId());
        if (existing.isEmpty()) {
            address.setIsDefault(true);
        }

        address.setUser(user);
        return addressRepository.save(address);
    }

    @Transactional
    public UserAddress updateAddress(Long userId, Long addressId, UserAddress updatedAddress) {
        UserAddress existing = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!existing.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to update this address");
        }

        if (Boolean.TRUE.equals(updatedAddress.getIsDefault())) {
            resetDefaultAddresses(userId);
        }

        existing.setAddress(updatedAddress.getAddress());
        existing.setCity(updatedAddress.getCity());
        existing.setState(updatedAddress.getState());
        existing.setPincode(updatedAddress.getPincode());
        existing.setLatitude(updatedAddress.getLatitude());
        existing.setLongitude(updatedAddress.getLongitude());
        existing.setTag(updatedAddress.getTag());
        existing.setIsDefault(updatedAddress.getIsDefault());

        return addressRepository.save(existing);
    }

    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        UserAddress existing = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!existing.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this address");
        }

        addressRepository.delete(existing);

        // If we deleted the default address, make another one default
        if (Boolean.TRUE.equals(existing.getIsDefault())) {
            List<UserAddress> remaining = addressRepository.findByUserId(userId);
            if (!remaining.isEmpty()) {
                remaining.get(0).setIsDefault(true);
                addressRepository.save(remaining.get(0));
            }
        }
    }

    private void resetDefaultAddresses(Long userId) {
        List<UserAddress> addresses = addressRepository.findByUserId(userId);
        addresses.forEach(a -> a.setIsDefault(false));
        addressRepository.saveAll(addresses);
    }
}
