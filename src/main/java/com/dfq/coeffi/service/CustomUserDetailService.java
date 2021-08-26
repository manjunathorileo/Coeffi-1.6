/**
 * 
 */
package com.dfq.coeffi.service;

import com.dfq.coeffi.entity.user.CustomUserDetails;
import com.dfq.coeffi.entity.user.User;
import com.dfq.coeffi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;


/**
 * @author H Kapil Kumar
 *
 */

@Service("userDetailsService")
public class CustomUserDetailService implements UserDetailsService
{
	@Autowired
	private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findUserByEmail(email);
        userOptional
                .orElseThrow(() -> new UsernameNotFoundException("user dose not exist "+email));
        return userOptional.map(user -> new CustomUserDetails(user)).get();
    }

}
