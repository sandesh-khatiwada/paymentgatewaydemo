package com.sandesh.paymentgatewaydemo.service.user;


import com.sandesh.paymentgatewaydemo.dto.UserDTO;
import com.sandesh.paymentgatewaydemo.entity.User;
import com.sandesh.paymentgatewaydemo.exception.InvalidAccessException;
import com.sandesh.paymentgatewaydemo.mapper.UserMapper;
import com.sandesh.paymentgatewaydemo.repository.UserRepository;
import com.sandesh.paymentgatewaydemo.util.ApiResponse;
import com.sandesh.paymentgatewaydemo.util.EmailExtractorUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;



@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public ResponseEntity<ApiResponse<UserDTO>> getUserDetails(){

        String email = EmailExtractorUtil.getEmailFromJwt();

        User user = userRepository.findByEmail(email).orElseThrow(()->new InvalidAccessException("Unauthorized"));

        UserDTO responseDTO = userMapper.toDTO(user);

        ApiResponse<UserDTO> response = new ApiResponse<>(
                HttpStatus.OK,
                "User retrieved successfully",
                responseDTO
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
