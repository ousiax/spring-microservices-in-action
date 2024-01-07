package com.optimagrowth.license.events.handler;

import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.optimagrowth.license.events.model.OrganizationChangeModel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class OrganizationChangeHandler {

    @Bean
    Consumer<OrganizationChangeModel> inboundOrgChanges() {
        return organization -> {
            log.debug("Received a message of type " + organization.getType());

            switch (organization.getAction()) {
                case "GET":
                    log.debug("Received a GET event from the organization service for organization id {}",
                            organization.getOrganizationId());
                    break;
                case "SAVE":
                    log.debug("Received a SAVE event from the organization service for organization id {}",
                            organization.getOrganizationId());
                    break;
                case "UPDATE":
                    log.debug("Received a UPDATE event from the organization service for organization id {}",
                            organization.getOrganizationId());
                    break;
                case "DELETE":
                    log.debug("Received a DELETE event from the organization service for organization id {}",
                            organization.getOrganizationId());
                    break;
                default:
                    log.error("Received an UNKNOWN event from the organization service of type {}",
                            organization.getType());
                    break;
            }
        };
    }
}
