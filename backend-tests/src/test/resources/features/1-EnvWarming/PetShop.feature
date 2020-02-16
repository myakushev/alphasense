Feature: PetShop

########################################################################################################################

@category-one
  Scenario: Add pet into shop
    And set unique pet id in context
    And send create pet request
      """
        {
          "id": ${petId},
          "category": {
            "id": 1101,
            "name": "Some Category Name"
          },
          "name": "Some Pet Name",
          "photoUrls": [
            "name:http:\\someUrl.com"
          ],
          "tags": [
            {
              "id": 99,
              "name": "Some Tag Name"
            }
          ],
          "status": "available"
        }
      """
    And check response 200
      """
        {
          "id": ${petId},
          "category": {
            "id": 1101,
            "name": "Some Category Name"
          },
          "name": "Some Pet Name",
          "photoUrls": [
            "name:http:\\someUrl.com"
          ],
          "tags": [
            {
              "id": 99,
              "name": "Some Tag Name"
            }
          ],
          "status": "available"
        }
      """
    And check creation of pet with id "${petId}"
    And send delete pet request for "${petId}"
    And check deleting of pet with id "${petId}"

########################################################################################################################

  @category-one
  Scenario: Delete pet from the shop and check
    And set unique pet id in context
    And send create pet request
      """
        {
          "id": ${petId},
          "category": {
            "id": 1101,
            "name": "Some Category Name"
          },
          "name": "Some Pet Name",
          "photoUrls": [
            "name:http:\\someUrl.com"
          ],
          "tags": [
            {
              "id": 99,
              "name": "Some Tag Name"
            }
          ],
          "status": "available"
        }
      """
    And check response 200
      """
        {
          "id": ${petId},
          "category": {
            "id": 1101,
            "name": "Some Category Name"
          },
          "name": "Some Pet Name",
          "photoUrls": [
            "name:http:\\someUrl.com"
          ],
          "tags": [
            {
              "id": 99,
              "name": "Some Tag Name"
            }
          ],
          "status": "available"
        }
      """
    And send delete pet request for "${petId}"
    And check deleting of pet with id "${petId}"

########################################################################################################################

  @category-two
  Scenario: Create order and check status
    And set unique order id in context
    And send create order request
      """
        {
          "id": ${orderId},
          "petId": 401453232,
          "quantity": 1,
          "shipDate": "${shipDate = <now, yyyy-MM-dd'T'HH:mm:ss.SSS>+0000}",
          "status": "placed",
          "complete": true
        }
      """
    And check response 200
      """
        {
          "id" : ${orderId},
          "petId" : 401453232,
          "quantity" : 1,
          "shipDate" : "${shipDate}",
          "status" : "placed",
          "complete" : true
        }
      """
    And check creation of order with id "${orderId}"

########################################################################################################################