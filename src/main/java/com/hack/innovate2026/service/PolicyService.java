package com.hack.innovate2026.service;

import com.hack.innovate2026.dto.request.CreatePolicyRequest;
import com.hack.innovate2026.dto.request.UpdatePolicyRequest;
import com.hack.innovate2026.dto.response.PolicyResponse;
import com.hack.innovate2026.entity.Policy;
import com.hack.innovate2026.exception.ConflictException;
import com.hack.innovate2026.exception.ResourceNotFoundException;
import com.hack.innovate2026.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PolicyService {

    private final PolicyRepository policyRepository;

    @Transactional(readOnly = true)
    public List<PolicyResponse> getAllPolicies() {
        return policyRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PolicyResponse getPolicyById(Long id) {
        return toResponse(getPolicy(id));
    }

    @Transactional(readOnly = true)
    public List<PolicyResponse> getPoliciesByCategory(String category) {
        return policyRepository.findByCategory(category)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public PolicyResponse createPolicy(CreatePolicyRequest request) {
        if (!policyRepository.findByCategory(request.category()).isEmpty()) {
            throw new ConflictException("Policy already exists for category: " + request.category());
        }

        Policy policy = Policy.builder()
                .category(request.category())
                .maxAmount(request.maxAmount())
                .companyLimit(request.companyLimit())
                .active(true)
                .build();

        return toResponse(policyRepository.save(policy));
    }

    @Transactional
    public PolicyResponse updatePolicy(Long id, UpdatePolicyRequest request) {
        Policy policy = getPolicy(id);

        if (request.maxAmount() != null) {
            policy.setMaxAmount(request.maxAmount());
        }
        if (request.companyLimit() != null) {
            policy.setCompanyLimit(request.companyLimit());
        }
        if (request.active() != null) {
            policy.setActive(request.active());
        }

        return toResponse(policyRepository.save(policy));
    }

    private Policy getPolicy(Long id) {
        return policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found with id: " + id));
    }

    private PolicyResponse toResponse(Policy policy) {
        return new PolicyResponse(
                policy.getId(),
                policy.getCategory(),
                policy.getMaxAmount(),
                policy.getCompanyLimit(),
                policy.getActive(),
                policy.getCreatedAt(),
                policy.getUpdatedAt()
        );
    }
}
