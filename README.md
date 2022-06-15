
# income-tax-calculation

This is where we make Downstream calls to initiate a calculation of a user's tax return, and to declare that they wish to crystallise/submit their return. We can also use Calculation to retrieve the Business Details of a particular NINO, required for the valid tax year list session values used across the service. Income Tax View and Change also call down to this repo for some requests.

## Running the service locally

You will need to have the following:
- Installed/configured [service manager](https://github.com/hmrc/service-manager).

The service manager profile for this service is:

    sm --start INCOME_TAX_CALCULATION_VC
Run the following command to start the remaining services locally:

    sudo mongod (If not already running)
    sm --start INCOME_TAX_SUBMISSION_ALL -r

This service runs on port: `localhost:9314`

### Calculation endpoints:

- **GET     /income-tax/view/calculations/liability/:nino/:calculationId** (IF Call) The IF call to retrieve calculation details for a particular Calculation ID

- **GET     /income-tax/list-of-calculation-results/:nino?taxYear=:taxYear** (DES Call) Retrieves list of Calculation results for a particular user, either with or without a tax year

- **GET     /registration/business-details/nino/:nino**  (DES Call) Retrieves the Business Details for a particular NINO. Used to obtain a list of Valid Tax Years that a user can submit against.

- **POST    /income-tax/nino/$nino/taxYear/$taxYear/tax-calculation** (DES Call) Sends off Calculation request and Retrieves Calculation ID 

- **POST    /income-tax/nino/$nino/taxYear/$taxYear/tax-calculation?crystallise=true** (DES Call) Sends off Calculation request and Retrieves Calculation ID, declaring Intent to Crystallise

- **POST    /income-tax/calculation/nino/:nino/:taxYear/:calculationId/crystallise** (DES Call) Declare Crystallisation (Submit Self Assessment for that Tax Year)

### Downstream services
All Calculation requests / data is retrieved / updated via one of two downstream systems.
- DES (Data Exchange Service)
- IF (Integration Framework)

### Connected microservices
- income-tax-view-change-frontend (Is called out to once data is retrieved)
- income-tax-submission-frontend (Makes triggering calls)

## Ninos with stub data for Calculation Related Calls

### Get Calculation List Responses (For the Following Call, a * symbol represents any number, a # represents any letter)

| Nino | Response Status | Notes
| --- | --- | --- |
| L#*****2# | 500 (Server Error) |  |
| L#******# | 404 (Not Found) |  |
| A#*****1# | 200 (Ok) | Returns a Calculation List response (Crystallised) |
| Any Other Nino | 200 (Ok) | Returns a Calculation List response |

### Intent to Crystallise BVR Errors
| Nino | Response Status | Associated BVR Error
| --- | --- | --- |
| AA004031A | 403 (Forbidden) | No updates provided |
| AA004091A | 409 (Conflict) | We already have an Income Tax Return for that tax year |
| AA004221A | 422 (Unprocessable Entity) | There’s a problem with your updates |


### Declare Crystallisation BVR Errors
| Nino | Response Status | Associated BVR Error
| --- | --- | --- |
| AA104091A | 409 (Conflict) | Your address has changed |
| AA104092A | 409 (Conflict) | We already have an Income Tax Return for that tax year |
| AA104093A | 409 (Conflict) | Your Income Tax Return has been updated |
| AA104221A | 422 (Unprocessable Entity) | No business income sources |


### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").