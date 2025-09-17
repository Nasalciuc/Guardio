# Requirements Document

## Introduction

MScan is a native Android cybersecurity module designed to integrate seamlessly into the Moldovan government's "M" brand ecosystem (alongside MPass, MDelivery). The application provides comprehensive security scanning capabilities, cybersecurity news aggregation, and incident reporting functionality to protect users from digital threats while maintaining government-level security standards and GDPR compliance.

## Requirements

### Requirement 1: Security Scanner

**User Story:** As a mobile user, I want to scan files, URLs, and text content for security threats, so that I can safely interact with digital content without risking malware infection or phishing attacks.

#### Acceptance Criteria

1. WHEN a user inputs a URL THEN the system SHALL scan the URL using three separate APIs (VirusTotal, Google Safe Browsing, URLScan.io) and return a consolidated verdict
2. WHEN a user uploads a file THEN the system SHALL scan the file using the same three APIs and provide a security assessment
3. WHEN a user inputs text content THEN the system SHALL extract and scan any URLs found within the text
4. WHEN any of the three APIs flag content as malicious THEN the system SHALL return a "malicious" verdict
5. WHEN all APIs return safe results THEN the system SHALL return a "safe" verdict
6. WHEN results are mixed or inconclusive THEN the system SHALL return a "suspicious" verdict
7. WHEN a scan is completed THEN the system SHALL log all scan activities and results to the PocketBase backend

### Requirement 2: Email Breach Check

**User Story:** As a security-conscious user, I want to check if my email address has been compromised in known data breaches, so that I can take appropriate action to secure my accounts.

#### Acceptance Criteria

1. WHEN a user first accesses the email breach check THEN the system SHALL display a dialog asking "Doriți să efectuați o verificare de securitate a e-mailului dvs.?" with an option to enable weekly automatic checks
2. WHEN a user enters an email address THEN the system SHALL query the "Have I Been Pwned" API to check for known breaches
3. WHEN the check is completed THEN the system SHALL send a notification with the results
4. IF automatic weekly checks are enabled THEN the system SHALL perform the check weekly and notify the user of results
5. WHEN breach data is found THEN the system SHALL display the list of affected services and breach dates
6. WHEN the check is performed THEN the system SHALL log the check to the email_checks collection without storing the email address

### Requirement 3: Cybersecurity News Feed

**User Story:** As a user interested in cybersecurity awareness, I want to access the latest cybersecurity news and alerts from official Moldovan sources, so that I can stay informed about current threats and security recommendations.

#### Acceptance Criteria

1. WHEN the user accesses the news feed THEN the system SHALL display articles from stisc.gov.md and cyberevent.gov.md RSS feeds
2. WHEN the news feed loads THEN the system SHALL display 4 articles initially in a grid layout with 2 columns
3. WHEN the user taps the 4th item (+ icon) THEN the system SHALL load additional articles into the feed
4. WHEN a user taps an article THEN the system SHALL open the article in an in-app WebView
5. WHEN fetching news THEN the system SHALL implement caching with a 10-minute TTL to prevent excessive requests
6. IF push notifications are enabled THEN the system SHALL send notifications for important security alerts
7. WHEN the user accesses notification settings THEN the system SHALL provide a toggle to enable/disable security alert notifications

### Requirement 4: Incident Reporting

**User Story:** As a user who encounters suspicious content, I want to report potential security threats with supporting evidence, so that authorities can investigate and protect other users from similar threats.

#### Acceptance Criteria

1. WHEN a user accesses the report function THEN the system SHALL display a form with fields for problem type, description, screenshot upload, and email
2. WHEN a user completes a scan that identifies malicious content THEN the system SHALL display a toggle/button to report the threat with one tap
3. WHEN a user submits a report THEN the system SHALL save the report data to the PocketBase reports collection
4. WHEN attaching a screenshot THEN the system SHALL convert the image to Base64 format for storage
5. WHEN a report is submitted THEN the system SHALL return a confirmation message to the user
6. WHEN storing reports THEN the system SHALL ensure no PII is permanently stored beyond what's necessary for the report

### Requirement 5: Android OS Integration

**User Story:** As an Android user, I want MScan to integrate seamlessly with my device's sharing and browsing capabilities, so that I can quickly scan content from any app or set MScan as my security-first browser.

#### Acceptance Criteria

1. WHEN MScan is installed THEN the system SHALL register two share intents: "Scan with MScan" and "Report with MScan"
2. WHEN a user shares content via "Scan with MScan" THEN the system SHALL immediately initiate a security scan
3. WHEN a user shares content via "Report with MScan" THEN the system SHALL open the reporting form with the content pre-attached
4. WHEN MScan is set as default browser THEN the system SHALL intercept link clicks, perform quick scans, and either block malicious links or open safe links in the user's preferred browser
5. WHEN a malicious link is detected THEN the system SHALL show a warning but allow experienced users to override with a hidden toggle
6. WHEN the user configures a lock screen shortcut THEN the system SHALL provide instant access to QR code/text scanning via camera

### Requirement 6: Multilingual Support

**User Story:** As a user in Moldova, I want to use MScan in my preferred language (Romanian, English, or Russian), so that I can fully understand all security information and interface elements.

#### Acceptance Criteria

1. WHEN the application launches THEN the system SHALL detect the device language and display the interface in Romanian, English, or Russian accordingly
2. WHEN the user changes language settings THEN the system SHALL update all UI elements, messages, and notifications to the selected language
3. WHEN displaying security verdicts and alerts THEN the system SHALL present all information in the user's selected language
4. WHEN showing news articles THEN the system SHALL maintain original article language but display interface elements in the user's selected language

### Requirement 7: Security and Privacy Compliance

**User Story:** As a user of a government-level security application, I want my personal data to be protected according to GDPR standards and government security requirements, so that my privacy is maintained while using the security features.

#### Acceptance Criteria

1. WHEN the system processes any user data THEN it SHALL comply with GDPR requirements and government security standards
2. WHEN storing scan results THEN the system SHALL NOT store any personally identifiable information
3. WHEN handling API keys and credentials THEN the system SHALL store them as environment variables or in secure configuration files excluded from version control
4. WHEN logging activities THEN the system SHALL only log necessary technical information without personal identifiers
5. WHEN a user requests data deletion THEN the system SHALL provide mechanisms to remove their data from local storage
6. WHEN transmitting data to external APIs THEN the system SHALL use secure HTTPS connections and follow data minimization principles