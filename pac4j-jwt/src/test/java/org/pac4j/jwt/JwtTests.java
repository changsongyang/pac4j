/*
  Copyright 2012 - 2015 pac4j organization

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.pac4j.jwt;

import org.junit.Test;
import org.pac4j.core.exception.TechnicalException;

import org.pac4j.core.profile.UserProfile;
import org.pac4j.http.credentials.TokenCredentials;
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator;
import org.pac4j.jwt.profile.JwtGenerator;
import org.pac4j.oauth.profile.facebook.FacebookAttributesDefinition;
import org.pac4j.oauth.profile.facebook.FacebookProfile;

import static org.junit.Assert.*;

/**
 * This class tests the {@link JwtGenerator} and {@link org.pac4j.jwt.credentials.authenticator.JwtAuthenticator}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class JwtTests {

    private final static String KEY = "12345678901234567890123456789012";
    private final static String ID = "technicalId";
    private final static String NAME = "fakeName";
    private final static boolean VERIFIED = true;
    private final static String CLIENT_NAME = "clientName";

    @Test
    public void testGenericJwt() {
        final String token =
                "ZXlKMGVYQWlPaUpLVjFRaUxDSmhiR2NpT2lKSVV6STFOaUo5LmV5SnBjM01pT2lKRGRYTjBiMjBnU2xkVUlFSjFhV3hrWlhJaUxDSnBZWFFpT2pFME5UQXhOalEwTlRVc0ltVjRjQ0k2TVRRNE1UY3dNRFExTlN3aVlYVmtJam9pYUhSMGNITTZMeTluYVhSb2RXSXVZMjl0TDNCaFl6UnFJaXdpYzNWaUlqb2lkWE5sY2tCd1lXTTBhaTV2Y21jaUxDSmxiV0ZwYkNJNkluVnpaWEpBY0dGak5Hb3ViM0puSW4wLnpPUGI3cmJJM0lZN2lMWFRLMTI2R2d1MlEzcE5DWnNVenpnemdzcVI3eFU=";

        final TokenCredentials credentials = new TokenCredentials(token, JwtAuthenticator.class.getName());
        final JwtAuthenticator authenticator = new JwtAuthenticator(KEY);
        authenticator.validate(credentials);
        assertNotNull(credentials.getUserProfile());
    }

    @Test
    public void testGenerateAuthenticate() {
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<FacebookProfile>(KEY);
        final FacebookProfile profile = createProfile();
        final String token = generator.generate(profile);
        assertToken(profile, token);
    }

    @Test
    public void testGenerateAuthenticateNotEncrypted() {
        final JwtGenerator<FacebookProfile> generator = new JwtGenerator<FacebookProfile>(KEY, false);
        final FacebookProfile profile = createProfile();
        final String token = generator.generate(profile);
        assertToken(profile, token);
        final JwtAuthenticator authenticator = new JwtAuthenticator(KEY);
    }

    private void assertToken(FacebookProfile profile, String token) {
        final TokenCredentials credentials = new TokenCredentials(token, CLIENT_NAME);
        final JwtAuthenticator authenticator = new JwtAuthenticator(KEY);
        authenticator.validate(credentials);
        final UserProfile profile2 = credentials.getUserProfile();
        assertTrue(profile2 instanceof FacebookProfile);
        final FacebookProfile fbProfile = (FacebookProfile) profile2;
        assertEquals(profile.getTypedId(), fbProfile.getTypedId());
        assertEquals(profile.getFirstName(), fbProfile.getFirstName());
        assertEquals(profile.getDisplayName(), fbProfile.getDisplayName());
        assertEquals(profile.getFamilyName(), fbProfile.getFamilyName());
        assertEquals(profile.getVerified(), fbProfile.getVerified());
    }

    private FacebookProfile createProfile() {
        final FacebookProfile profile = new FacebookProfile();
        profile.setId(ID);
        profile.addAttribute(FacebookAttributesDefinition.NAME, NAME);
        profile.addAttribute(FacebookAttributesDefinition.VERIFIED, VERIFIED);
        return profile;
    }

    @Test(expected = TechnicalException.class)
    public void testAuthenticateFailed() {
        final JwtAuthenticator authenticator = new JwtAuthenticator(KEY);
        final TokenCredentials credentials = new TokenCredentials("fakeToken", CLIENT_NAME);
        authenticator.validate(credentials);
    }
}
