package com.optimagrowth.organization.events.source;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

import com.optimagrowth.organization.events.model.OrganizationChangeModel;
import com.optimagrowth.organization.utils.UserContextHolder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SimpleSourceBean {
    private final String BINDING_NAME = "output";

    private StreamBridge source;

    public SimpleSourceBean(StreamBridge source) {
        this.source = source;
    }

    public void publishOrganizationChange(ActionEnum action, String organizationId) {
        log.debug("Sending Kafka message {} for Organization Id: {}", action, organizationId);
        OrganizationChangeModel change = new OrganizationChangeModel(
                OrganizationChangeModel.class.getTypeName(),
                action.toString(),
                organizationId,
                UserContextHolder.getContext().getCorrelationId());
        source.send(BINDING_NAME, change);
    }
}