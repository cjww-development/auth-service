// Copyright (C) 2011-2012 the original author or authors.
// See the LICENCE.txt file distributed with this work for additional
// information regarding copyright ownership.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package mocks

import models.accounts.{BasicDetails, Enrolments, Settings, UserProfile}
import models.auth.{AuthContext, AuthContextDetails, User}
import org.joda.time.DateTime
import utils.security.DataSecurity

trait MockModels {

  val testBasicDetails =
    BasicDetails(
      "testFirstName",
      "testLastName",
      "testUserName",
      "test@email.com",
      Some(Map("createdAt" -> DateTime.now()))
    )

  implicit val testContext =
    AuthContext(
      "context-1234567890",
      User(
        "user-766543",
        "testFirstName",
        "testLastName"
      ),
      "testLink",
      "testLink",
      "testLink"
    )

  val testSettings =
    Settings(
      Some("user"),
      Some("#124AAO"),
      Some("/test/Link")
    )

  val testProfile =
    UserProfile(
      "testFirstName",
      "testLastName",
      "testUserName",
      "test@email.com"
    )

  val testContextDetails =
    AuthContextDetails(
      "context-1234567890",
      "testFirstName",
      "testLastName"
    )

  val testEnrolments =
    Enrolments(
      Some("testHubId"),
      Some("testDiagId"),
      Some("testDevId")
    )

  val testEncContextDetails = DataSecurity.encryptData[AuthContextDetails](testContextDetails).get

  val testEncFeedList = "NHb63SXRUGosU2HezDjp/En1mS0Ab2c4C7R8rhd2BnxVJie2aaBH+HgKlJpzKVO0GeO4oU6sM8Tb8lMY7ggpniq8Go5idu3qsfbCNLpqp9NrrhL83bGM3q/Ji2GAz64Uf6tS5qOo4KxKOZPH35XGd60n+AIfQ3RTM1/xjwRf2WzzzVui2o/Axnv8P+6D/RTeyKZgaxY5QWjsTODgV1LbYWhrrrXxwGcTbO08bIDdczcDb2vVSUhBY4tVK2DsImfxnH4itDUvk709VjZSb7ABND99Cfznpr/14fbnvmfIw51fKJnowLa+jmELTCvSS/KYoI521jS0zvcRc2rLSOeIxawXH3BqxLO50Videkljqs2DIfD+1VyUsX/AOGaoGEWDGalLRiZ9o7Mi+hC/7E6GsdPPB56RubfE8WR2/GVOHjsBtwyR/VQrQ2HRXG5btCu99AIDEB38Jiufea1pRS/dqV9s9jQywPqeLOz/GgJ97uLWN+kRMdlz2G4nQbCeZv17nis7W3ghCJ4i6gfm1gYAm5kLbcsKu0ptEAzG0QO7r0OHMF+52jkcY+BuupOphXkvP0LpNLKoClDWFzriTyjZqUAFwFnk0fLCPXkfUwfsKME2oRkn6tqVcEPFAYzgjUESEcgjfSQVxfUuUhmA4YN2Ph9IWH8r6zaulp5VxeyjU8EX6wzHOuwChO1KowsKgIcbipejaNGGj8+g1Mac2n3PjF3GZfFMxRt1+4NxIW1HSKxGCSapb85mAb7P9DgAAcHtrZjxjphY308vEHY2kn9tRc/KfNRt/h1niVZr8cDz3J82v1eyS7VRI8BUaxieeW1/3vduB1LAdydv2wuH3dpAsOLtOymRGCEaMk1cdPuOIYpCbQZjsyMBsv3ih1vIXXJr883VogvBZM+CPeruUcJ6PoWi2pDm+0+lUiC0zOZ1Ntij9wouBT95nkKEdo5aOf5uOa2/W1SnDqsnAdmvq+24skrKEBINjPkD3a57OuYrM8swiwwP1lkadmdk15Km8qvMkENNyvaFb1lp8A1/guANQ13bPNC42RJNI+qPEl0YY44n4z11buY/veeWgES1k8F1VC/fVTKun8lYp8+h+pLb598dm7JVdapOhRCB1ssgVsKa7oozoo2iQXM0vIGBrJS3oUBEQp5Rwai26Tt+Cwv/19l1kFOZTuc/HuaGeqTZILltPHvsDdAIDBZ6A1T2wOrIx9nx8V44viix9v01jw3AxDgymXBJmAJF3EUuOQNsUxvBjnfzhe0JtFlPEzSnzBFciQrsPFMZLliS0JEr2YhXgw=="
}
