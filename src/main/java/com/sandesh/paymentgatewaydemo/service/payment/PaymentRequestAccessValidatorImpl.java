package com.sandesh.paymentgatewaydemo.service.payment;

import com.sandesh.paymentgatewaydemo.entity.PaymentRequest;
import com.sandesh.paymentgatewaydemo.entity.User;
import com.sandesh.paymentgatewaydemo.exception.InvalidAccessException;
import com.sandesh.paymentgatewaydemo.repository.UserRepository;
import com.sandesh.paymentgatewaydemo.service.cache.PaymentCacheService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

//this service verifies if the payment request is accessible to the user
// (user can only access payment requests initiated by him and not other users)

@Service
@AllArgsConstructor
public class PaymentRequestAccessValidatorImpl implements PaymentRequestAccessValidator {

    private final UserRepository userRepository;
    private final PaymentCacheService paymentCacheService;

    @Override
    public boolean isPaymentRequestAccessValid(String email, String refId){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));


        PaymentRequest paymentRequest = paymentCacheService.getPendingPayment(refId);

        if(paymentRequest==null){
            throw new InvalidAccessException("Transaction request not found for refId: "+refId);
        }

        if(!Objects.equals(user.getId(),paymentRequest.getUserId())){
            throw new InvalidAccessException("Invalid transaction reference or transaction request has been expired");
        }

        return true;
    }


}
