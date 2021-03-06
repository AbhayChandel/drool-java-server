package com.hexlindia.drool.user.services.impl.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hexlindia.drool.user.business.api.usecase.UserProfile;
import com.hexlindia.drool.user.dto.UserProfileDto;
import com.hexlindia.drool.user.filters.JwtValidationFilter;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = UserProfileRestServiceImpl.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {WebSecurityConfigurer.class, JwtValidationFilter.class}),
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
class UserProfileRestServiceImplTest {

    @Value("${rest.uri.version}")
    String restUriVersion;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserProfile userProfileMock;

    @Test
    public void findById_HttpMethodNotAllowedError() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.put(getFindByIdUri() + "/1"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void findById_ParametersMissing() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post(getFindByIdUri() + "/"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void findById_ParametersPassedToBusinessLayer() throws Exception {
        ObjectId accountId = new ObjectId();
        this.mockMvc.perform(MockMvcRequestBuilders.get(getFindByIdUri() + "/" + accountId.toHexString()));
        ArgumentCaptor<String> idArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(this.userProfileMock, times(1)).findById(idArgumentCaptor.capture());
        assertEquals(accountId.toHexString(), idArgumentCaptor.getValue());
    }

    @Test
    public void update_HttpMethodNotAllowedError() throws Exception {
        UserProfileDto userProfileTo = new UserProfileDto();
        this.mockMvc.perform(MockMvcRequestBuilders.post(getUpdateUri()))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void update_missingRequestObject() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.put(getUpdateUri())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void update_ParametersPassedToBusinessLayer() throws Exception {
        UserProfileDto userProfileDto = new UserProfileDto();
        userProfileDto.setGender("M");
        userProfileDto.setCity("Jaipur");
        ObjectId accountId = new ObjectId();
        userProfileDto.setId(accountId.toHexString());
        userProfileDto.setName("Priya Gupta");
        String requestBody = objectMapper.writeValueAsString(userProfileDto);
        this.mockMvc.perform(MockMvcRequestBuilders.put(getUpdateUri())
                .content(requestBody).contentType(MediaType.APPLICATION_JSON));

        ArgumentCaptor<UserProfileDto> userProfileDtoArgumentCaptor = ArgumentCaptor.forClass(UserProfileDto.class);
        verify(this.userProfileMock, times(1)).update(userProfileDtoArgumentCaptor.capture());
        assertEquals("M", userProfileDtoArgumentCaptor.getValue().getGender());
        assertEquals("Jaipur", userProfileDtoArgumentCaptor.getValue().getCity());
        assertEquals(accountId.toHexString(), userProfileDtoArgumentCaptor.getValue().getId());
        assertEquals("Priya Gupta", userProfileDtoArgumentCaptor.getValue().getName());
    }

    private String getFindByIdUri() {
        return "/" + restUriVersion + "/view/user/profile/find/id";
    }

    private String getFindUsernameUri() {
        return "/" + restUriVersion + "/accessall/user/profile/find/username";
    }

    private String getUpdateUri() {
        return "/" + restUriVersion + "/user/profile/update";
    }

}