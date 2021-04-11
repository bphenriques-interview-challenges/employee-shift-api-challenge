# Employee shifts

Goal: Build a backend service for employee shift management. 

Part 1: Small web API
Part 2: Containerize it

##  Backend service

Create a web API that manages employee shifts, providing the following actions:

1. Create, read, update, and delete employee details.
2. Create, read, update, and delete shifts for a single employee.
3. Create, read, update, and delete shifts for multiple employees.
4. Swap shifts between two employees.

Business rules:
1. All employees must have a first name, a last name, and a unique address.
2. An employee cannot work more than 1 shift at a time.

## Containerization

Containerize the web API emulating the following scenario – consider that your web API could not be developed in a cloud native way from the start, so you’ll have to make it cloud friendly by deploying it using the sidecar pattern to enhance the API with liveness and readiness probes. You can also propose alternatives to the sidecar pattern if you think that there is a better solution.
