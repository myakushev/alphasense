@category-all

Feature: PetShop

#########################################################################################################################

  @category-one
  Scenario Outline: Add pet into shop (json as string): <petStatus> pet
    Given set unique pet id in context
    And set parameters
      | categoryId   | categoryName   | petName   | tagId   | tagName   | petStatus   |
      | <categoryId> | <categoryName> | <petName> | <tagId> | <tagName> | <petStatus> |
    And send create pet request
      """
        {
          "id": ${petId}123,
          "category": {
            "id": ${categoryId},
            "name": "${categoryName}"
          },
          "name": "${petName}",
          "photoUrls": [
            "name:http://someUrl1.com",
            "name:http://someUrl2.com"
          ],
          "tags": [
            {
              "id": ${tagId},
              "name": "${tagName}"
            }
          ],
          "status": "${petStatus}"
        }
      """
    Then check response 200
      """
        {
          "id": ${petId},
          "category": {
            "id": ${categoryId},
            "name": "${categoryName}"
          },
          "name": "${petName}",
          "photoUrls": [
            "name:http://someUrl1.com",
            "name:http://someUrl2.com"
          ],
          "tags": [
            {
              "id": ${tagId},
              "name": "${tagName}"
            }
          ],
          "status": "${petStatus}"
        }
      """
    And check creation of pet with id "${petId}"
    And send delete pet request for "${petId}"
    And check deleting of pet with id "${petId}"
    Examples:
      | categoryId | categoryName           | petName            | tagId | tagName           | petStatus |
      | 1101       | Some category Name     | Some test Name     | 44    | Some tag name     | available |
      | 1102       | Another category Name  | Another test Name  | 45    | Another tag name  | pending   |
      | 1103       | One more category Name | One more test Name | 46    | One more tag name | sold      |

########################################################################################################################

  @category-one
  Scenario: Delete pet from the shop and check
    Given set unique pet id in context
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
            "name:http://someUrl.com"
          ],
          "tags": [
            {
              "id": 119,
              "name": "Some Tag Name"
            }
          ],
          "status": "available"
        }
      """
    Then check response 200
      """
        {
          "id": ${petId},
          "category": {
            "id": 1101,
            "name": "${anyValue}"
          },
          "name": "Some Pet Name",
          "photoUrls": [
            "name:${anyValue}"
          ],
          "tags": [
            {
              "id": 119,
              "name": "${anyValue}"
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
    Given set unique order id in context
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
    Then check response 200
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

  @category-two
  Scenario: Add pet into shop (POJO conversion example)
    Given set unique pet id in context
    And send create pet request with params
      | petId    | categoryId | categoryName       | petName       | petPhotoUrls                            | tagId | tagName       | petStatus |
      | ${petId} | 1101       | Some Category Name | Some Pet Name | http://someUrl1.com,http://someUrl2.com | 99    | Some Tag Name | available |
    And check create pet response 200
    And check creation of pet with id "${petId}"
    And send delete pet request for "${petId}"
    And check deleting of pet with id "${petId}"

########################################################################################################################