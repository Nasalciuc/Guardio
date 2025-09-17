# Implementation Plan

- [ ] 1. Set up backend infrastructure and database
  - [x] Set up Python FastAPI backend project structure
  - [ ] Configure PocketBase database and collections (scans, email_checks, reports)  
        (Initial JSON schema drafted in `backend/app/db/schema/collections.json`; automation script `backend/app/db/apply_collections.py` added – run after starting PocketBase)
  - [x] Set up development environment with proper build tools (virtualenv + dependencies installed) *(API keys still pending)*
  - [x] Create environment configuration for external APIs (`backend/.env.example` + `config.py`)
  - _Requirements: 7.3, 7.4_

- [ ] 2. Enhance existing Android data models
- [ ] 2.1 Extend existing data models for API integration
  - Add ScanResult, BreachResult data classes to existing SecurityModels.kt
  - Implement API response models for backend communication
  - Add validation functions for scan results and breach data
  - Create unit tests for new data model validation
  - _Requirements: 1.1, 2.1, 3.1, 4.1_

- [ ] 2.2 Define PocketBase collections schema
  - Create PocketBase collections for scans, email_checks, and reports
  - Implement collection validation rules and indexes
  - Write migration scripts for database schema
  - _Requirements: 1.7, 2.6, 4.4, 7.4_

- [ ] 2.3 Implement backend data models
  - Create Python Pydantic models for API request/response validation
  - Implement data sanitization and validation functions
  - Write unit tests for backend data models
  - _Requirements: 7.1, 7.3_

- [ ] 3. Build backend API services
- [ ] 3.1 Implement scan service with external API integration
  - Create FastAPI endpoint for POST /scan
  - Integrate with VirusTotal, Google Safe Browsing, and URLScan.io APIs
  - Implement async API calls and result aggregation logic
  - Add comprehensive error handling for API failures
  - Write unit tests for scan service
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7_

- [ ] 3.2 Implement email breach check service
  - Create FastAPI endpoint for POST /scan/email
  - Integrate with Have I Been Pwned API
  - Implement email hashing for privacy protection
  - Add logging to email_checks collection
  - Write unit tests for email breach service
  - _Requirements: 2.1, 2.2, 2.5, 2.6_

- [ ] 3.3 Implement news aggregation service
  - Create FastAPI endpoint for GET /news
  - Implement RSS feed parsing for stisc.gov.md and cyberevent.gov.md
  - Add caching mechanism with 10-minute TTL
  - Write unit tests for news service
  - _Requirements: 3.1, 3.2, 3.5_

- [ ] 3.4 Implement report submission service
  - Create FastAPI endpoint for POST /report
  - Implement Base64 screenshot handling and validation
  - Add report data storage to PocketBase reports collection
  - Write unit tests for report service
  - _Requirements: 4.1, 4.3, 4.4, 4.5_

- [ ] 4. Enhance existing Android UI with backend integration
- [ ] 4.1 Add email breach check functionality to SecurityScreen
  - Implement email breach check dialog in existing SecurityScreen
  - Add email input validation and API call integration
  - Implement weekly automatic check toggle and notification system
  - Write UI tests for email breach check flow
  - _Requirements: 2.1, 2.2, 2.3, 2.4_

- [ ] 4.2 Enhance UnifiedSearchComponent with real scanning
  - Connect existing UnifiedSearchComponent to backend scan API
  - Implement real-time scan results display and status updates
  - Add proper error handling and loading states
  - Implement scan result storage and history tracking
  - Write UI tests for enhanced scanning functionality
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7_

- [ ] 4.3 Enhance ReportProblemScreen with backend submission
  - Connect existing ReportProblemScreen to backend report API
  - Implement screenshot upload with Base64 encoding
  - Add form submission success/error handling
  - Implement report confirmation and tracking
  - Write UI tests for enhanced report submission
  - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5_

- [ ] 5. Implement Android OS integration features
- [ ] 5.1 Create share intent handlers
  - Implement ScanIntentActivity for "Scan with MScan" shares
  - Implement ReportIntentActivity for "Report with MScan" shares
  - Add proper intent filters to AndroidManifest.xml
  - Write integration tests for share intent handling
  - _Requirements: 5.1, 5.2, 5.3_

- [ ] 5.2 Implement default browser functionality
  - Create BrowserInterceptActivity for link interception
  - Implement quick scan and warning system for malicious links
  - Add override mechanism for experienced users
  - Write integration tests for browser interception
  - _Requirements: 5.4, 5.5_

- [ ] 5.3 Build lock screen camera shortcut
  - Implement QuickScanTileService for Quick Settings
  - Create CameraScanActivity with ML Kit QR code scanning
  - Add proper permissions and security handling
  - Write integration tests for camera scanning
  - _Requirements: 5.6_

- [ ] 6. Implement networking and repository layers
- [ ] 6.1 Add networking dependencies and create API client
  - Add Retrofit, OkHttp, and Gson dependencies to existing build.gradle.kts
  - Create API service interfaces for all backend endpoints (/scan, /scan/email, /news, /report)
  - Implement proper error handling and timeout configurations
  - Write unit tests for API client functionality
  - _Requirements: 1.1, 2.1, 3.1, 4.1_

- [ ] 6.2 Implement repository pattern for data management
  - Create repository classes extending existing data structure
  - Implement local caching using existing SecurityModels structure
  - Add offline capability and sync mechanisms for scan history
  - Write unit tests for repository operations
  - _Requirements: 1.7, 3.5, 7.4_

- [ ] 7. Add multilingual support and localization
- [ ] 7.1 Implement string resources for all languages
  - Create string resources for Romanian, English, and Russian
  - Implement language detection and switching logic
  - Add proper RTL support where applicable
  - Write tests for localization functionality
  - _Requirements: 6.1, 6.2, 6.3, 6.4_

- [ ] 8. Implement notification system
- [ ] 8.1 Create push notification infrastructure
  - Set up Firebase Cloud Messaging for push notifications
  - Implement notification preferences and toggle functionality
  - Create notification handlers for security alerts and email breach results
  - Write tests for notification delivery and handling
  - _Requirements: 2.3, 2.4, 3.6, 3.7_

- [ ] 9. Add security and privacy features
- [ ] 9.1 Implement GDPR compliance measures
  - Add data deletion mechanisms and user consent flows
  - Implement PII detection and removal systems
  - Create privacy policy integration and user controls
  - Write tests for privacy compliance features
  - _Requirements: 7.1, 7.2, 7.4, 7.5, 7.6_

- [ ] 9.2 Implement security hardening measures
  - Add certificate pinning for API communications
  - Implement root detection and security warnings
  - Add code obfuscation and anti-tampering measures
  - Write security tests and penetration testing scenarios
  - _Requirements: 7.3, 7.6_

- [ ] 10. Create comprehensive test suites
- [ ] 10.1 Implement unit tests for all components
  - Write unit tests for all Android ViewModels and repositories
  - Create unit tests for all backend services and utilities
  - Add data validation and edge case testing
  - Achieve minimum 80% code coverage
  - _Requirements: All requirements validation_

- [ ] 10.2 Implement integration and end-to-end tests
  - Create integration tests for API communication flows
  - Write end-to-end tests for complete user workflows
  - Add performance and load testing for backend services
  - Implement accessibility testing for Android UI
  - _Requirements: All requirements validation_

- [ ] 11. Finalize deployment and configuration
- [ ] 11.1 Set up production configuration
  - Configure production API keys and environment variables
  - Set up PocketBase production deployment configuration
  - Implement proper logging and monitoring systems
  - Create deployment scripts and documentation
  - _Requirements: 7.3, 7.4_

- [ ] 11.2 Prepare Android app for release
  - Configure app signing and release build settings
  - Implement crash reporting and analytics
  - Create app store listing materials and screenshots
  - Perform final security audit and compliance verification
  - _Requirements: 7.1, 7.6_