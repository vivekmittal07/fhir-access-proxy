/*
 * Copyright 2021-2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.fhir.gateway.plugin;

import ca.uhn.fhir.context.FhirContext;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.fhir.gateway.HttpFhirClient;
import com.google.fhir.gateway.interfaces.AccessChecker;
import com.google.fhir.gateway.interfaces.AccessCheckerFactory;
import com.google.fhir.gateway.interfaces.AccessDecision;
import com.google.fhir.gateway.interfaces.NoOpAccessDecision;
import com.google.fhir.gateway.interfaces.PatientFinder;
import com.google.fhir.gateway.interfaces.RequestDetailsReader;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This access-checker uses the `patient_id` and `scope` claims in the access token to decide
 * whether access to a request should be granted or not. The `scope` claims are expected to tbe
 * SMART-on-FHIR compliant.
 */
public class DemoAccessChecker implements AccessChecker {
  private static final Logger logger = LoggerFactory.getLogger(DemoAccessChecker.class);

  private final FhirContext fhirContext;

  private DemoAccessChecker(FhirContext fhirContext) {

    Preconditions.checkNotNull(fhirContext);
    this.fhirContext = fhirContext;
  }

  @Override
  public AccessDecision checkAccess(RequestDetailsReader requestDetails) {
    // For a Bundle requestDetails.getResourceName() returns null
    return new NoOpAccessDecision(true);
  }

  @Named(value = "demo")
  static class Factory implements AccessCheckerFactory {

    @VisibleForTesting static final String PATIENT_CLAIM = "patient_id";

    @VisibleForTesting static final String SCOPES_CLAIM = "scope";

    public AccessChecker create(
        DecodedJWT jwt,
        HttpFhirClient httpFhirClient,
        FhirContext fhirContext,
        PatientFinder patientFinder) {
      return new DemoAccessChecker(fhirContext);
    }
  }
}
