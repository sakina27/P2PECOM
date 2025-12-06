package com.example.userservice.dto;

public class NotificationResponse {
    private boolean success;
    private String channel;
    private String providerMessageId;
    private String error;

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }

    public String getProviderMessageId() { return providerMessageId; }
    public void setProviderMessageId(String providerMessageId) { this.providerMessageId = providerMessageId; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}
