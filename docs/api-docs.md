Follows the documentation of the API. Note that this is work in progress as:
- Should be automagically-docummented through Swagger using the OpenAPI Specification (OAS).
- Both happy paths and unhappy paths must be documented.

# Create, read, update, and delete employee details.

Schema:
- `first_name`: Non empty. Maximum size TBD.
- `last_name`: Non empty. Maximum size TBD.
- `address`: Non empty. Maximum size TBD.
- `id`: Unique identifier of the employee in UUID. One can argue that it can be a incremental id.

## Create

Request:
```json
$ POST /employee "Content-Type: application/json" "Accept: application/json"
{
    "first_name": "John",
    "last_name": "Doe",
    "address": "Somewhere"
}
```

Response (happy-path):
```json
HTTP STATUS CODE 201
{
    "id": "<uuid-formated-id>",
    "first_name": "John",
    "last_name": "Doe",
    "address": "Somewhere"
}
```

## Read

Request:
```json
GET /employee/{id} "Accept: application/json"
```

Response (happy-oath):
```json
HTTP STATUS CODE 200
{
    "id": "<uuid-formated-id>",
    "first_name": "John",
    "last_name": "Doe",
    "address": "Somewhere"
}
```

## Update
```json
PUT /employee/{id}/ "Content-Type: application/json" "Accept: application/json"
{
    "first_name": "John",
    "last_name": "Doe",
    "address": "Somewhere"
}
```

Response (happy-path):
```json
HTTP STATUS CODE 200
{
    "id": "<uuid-formated-id>",
    "first_name": "John",
    "last_name": "Doe",
    "address": "Somewhere"
}
```

## Delete
Request:
```json
DELETE /employee/{id}
```

Response (happy-path):
```
HTTP STATUS CODE 200
```

# Create, read, update, and delete shifts for single or multiple employee.

Schema:
- `from`: Date Time in ISO-8601 standard (UTC).
- `until`: Date Time in ISO-8601 standard (UTC). Must occurr after `from`.
- `employee_id`: Employee unique identifier.

## Create
Request:
```json
$ POST /shifts "Content-Type: application/json" "Accept: application/json"
[
    {
        "employee_id": "<uuid-formated-id>",
        "from": "2021-02-25T11:59Z",
        "until": "2021-02-25T11:59Z"
    }
]
```

Response (happy-path):
```json
HTTP STATUS CODE 200
[
    {
        "employee_id": "<uuid-formated-id>",
        "id": "<uuid-formated-id>",
        "from": "2021-02-25T11:59Z",
        "until": "2021-02-25T11:59Z"
    }
]
```

## Read:

Request:
```json
GET /shifts "Accept: application/json"
{
    "filter": {
        "employee_id": [
            "<user-id-1>",
            "<user-id-2>"
        ]
    }
}
```

Response (happy-path):
```json
HTTP STATUS CODE 200
[
    {
        "employee_id": "<uuid-formated-id>",
        "id": "<uuid-formated-id>",
        "from": "2021-02-25T11:59Z",
        "until": "2021-02-25T11:59Z"
    }
]
```

## Update

Request:
```json
POST /shifts "Content-Type: application/json" "Accept: application/json"
[
    {
        "id": "<uuid-formated-id>",
        "employee_id": "<uuid-formated-id>",
        "from": "2021-02-25T11:59Z",
        "until": "2021-02-25T11:59Z"
    }
]

````
Response (happy-path):
```json
HTTP STATUS CODE 200
[
    {
        "id": "<uuid-formated-id>",
        "employee_id": "<uuid-formated-id>",
        "from": "2021-02-25T11:59Z",
        "until": "2021-02-25T11:59Z"
    }
]
```

## Delete
Request:
```json
DELETE /shifts

[ "<shift-id-1>", "<shift-id-2>" ]
```

Response (happy-path):
```
HTTP STATUS CODE 200
```

# Swap shifts between two employees.

Schema:
- `shift_1`: Shift's unique identifier.
- `shift_2`: Shift's unique identifier.

Request:
```JSON
POST /shifts/swap-employees
{
    "shift_1": "<uuid-formated-id>",
    "shift_2": "<uuid-formated-id>"
}
```

Response (happy-path):
```json
HTTP STATUS CODE 200
[
    {
        "id": "<uuid-formated-id>",
        "employee_id": "<uuid-formated-id>",
        "from": "2021-02-25T11:59Z",
        "until": "2021-02-25T11:59Z"
    },
    {
        "id": "<uuid-formated-id>",
        "employee_id": "<uuid-formated-id>",
        "from": "2021-02-25T11:59Z",
        "until": "2021-02-25T11:59Z"
    }
]
```