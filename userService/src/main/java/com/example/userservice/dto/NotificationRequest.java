package com.example.userservice.dto;

import java.util.Map;

public class NotificationRequest {
    private String channel;
    private String recipient;
    private String templateId;
    private Map<String, String> templateParams;

    // getters / setters
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }
    public String getTemplateId() { return templateId; }
    public void setTemplateId(String templateId) { this.templateId = templateId; }
    public Map<String, String> getTemplateParams() { return templateParams; }
    public void setTemplateParams(Map<String, String> templateParams) { this.templateParams = templateParams; }
}
