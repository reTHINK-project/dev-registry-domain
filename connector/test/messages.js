var hypertyResourcesSchemes = {
  "type": "read",
  "from": "hyperty://hybroker.rethink.ptinovacao.pt/ff517bb0-832e-4326-883a-4d48c5e1afae",
  "to": "domain://registry.hybroker.rethink.ptinovacao.pt/",
  "body": {
    "resource": "user://gmail.com/openidtest10",
    "criteria": {
      "dataSchemes": ["comm"],
      "resources": ["chat"]
    }
  },
  "id": 3
};

var hypertyPerUser = {
  "type": "read",
  "from": "hyperty://hybroker.rethink.ptinovacao.pt/ff517bb0-832e-4326-883a-4d48c5e1afae",
  "to": "domain://registry.hybroker.rethink.ptinovacao.pt/",
  "body": {
      "resource": "user://gmail.com/openidtest10"
    },
  "id": 5
};

var dataObjectPerName = {
  "type": "read",
  "from": "hyperty://hybroker.rethink.ptinovacao.pt/ff517bb0-832e-4326-883a-4d48c5e1afae",
  "to": "domain://registry.hybroker.rethink.ptinovacao.pt/",
  "body": {
      "resource": "do-name1"
  },
  "id": 5
};

var dataObjectPerResources = {
  "type": "read",
  "from": "hyperty://hybroker.rethink.ptinovacao.pt/ff517bb0-832e-4326-883a-4d48c5e1afae",
  "to": "domain://registry.hybroker.rethink.ptinovacao.pt/",
  "body": {
    "resource": "do-name1",
    "criteria": {
      "resources": ["chat"]
    }
  },
  "id": 5
};

/*
 *var dataObjectPerReporter = {
 *  "type": "read",
 *  "from": "hyperty://hybroker.rethink.ptinovacao.pt/ff517bb0-832e-4326-883a-4d48c5e1afae",
 *  "to": "domain://registry.hybroker.rethink.ptinovacao.pt/",
 *  "body": {
 *      "resource": "hyperty://hybroker.rethink.ptinovacao.pt/ff517bb0-832e-4326-883a-4d48c5e1afae",
 *    },
 *  "id": 8
 *};
 */

var dataObjectPerUrl = {
  "type": "read",
  "from": "hyperty://hybroker.rethink.ptinovacao.pt/ff517bb0-832e-4326-883a-4d48c5e1afae",
  "to": "domain://registry.hybroker.rethink.ptinovacao.pt/",
  "body": {
      "resource": "comm://hybroker.rethink.ptinovacao.pt/77b3da83-0b5a-48e0-907b-363913b49920",
    },
  "id": 9
};

var createHyperty = {
  "id": 1,
  "from": "runtime://hybroker.rethink.ptinovacao.pt/1872/registry/",
  "to": "domain://registry.hybroker.rethink.ptinovacao.pt/",
  "type": "create",
  "body": {
      "idToken": null,
      "accessToken": null,
      "resource": null,
      "schema": null,
      "assertedIdentity": null,
      "value": {
            "user": "user://gmail.com/openidtest10",
            "hypertyDescriptorURL": "hyperty-catalogue://hybroker.rethink.ptinovacao.pt/.well-known/hyperty/GroupChatManager",
            "hypertyURL": "hyperty://hybroker.rethink.ptinovacao.pt/24a0724a-68ff-4f0d-ba2b-1e71911a7213",
            "expires": 3600,
            "resources": [
                    "chat"
                  ],
            "dataSchemes": [
                    "comm"
                  ]
          },
      "policy": "policy"
    }
};

var deleteHyperty = {
  "type": "delete",
  "from": "runtime://hybroker.rethink.ptinovacao.pt/1872/registry/",
  "to": "domain://registry.hybroker.rethink.ptinovacao.pt/",
  "body": {
      "value": {
            "user": "user://gmail.com/openidtest10",
            "hypertyURL": "hyperty://hybroker.rethink.ptinovacao.pt/24a0724a-68ff-4f0d-ba2b-1e71911a7213"
          }
    },
  "id": 4
};

var createDataObject = {
  "id": 2,
  "from": "runtime://hybroker.rethink.ptinovacao.pt/1872/registry/",
  "to": "domain://registry.hybroker.rethink.ptinovacao.pt/",
  "type": "create",
  "body": {
      "idToken": null,
      "accessToken": null,
      "resource": null,
      "schema": null,
      "assertedIdentity": null,
      "value": {
            "name": "myChat",
            "schema": "hyperty-catalogue://catalogue.hybroker.rethink.ptinovacao.pt/.well-known/dataschema/Communication",
            "url": "comm://hybroker.rethink.ptinovacao.pt/aa2f5bec-e3f7-471f-8ace-44c64edb8a6d",
            "expires": 3600,
            "reporter": "hyperty://hybroker.rethink.ptinovacao.pt/24a0724a-68ff-4f0d-ba2b-1e71911a7213",
            "preAuth": [],
            "subscribers": []
          },
      "policy": "policy"
    }
};

var deleteDataObject = {
  "type": "delete",
  "from": "runtime://hybroker.rethink.ptinovacao.pt/1872/registry/",
  "to": "domain://registry.hybroker.rethink.ptinovacao.pt/",
  "body": {
      "value": {
            "name": "comm://hybroker.rethink.ptinovacao.pt/aa2f5bec-e3f7-471f-8ace-44c64edb8a6d"
          }
    },
  "id": 8
};

module.exports = {
  createDataObject: createDataObject,
  deleteDataObject: deleteDataObject,
  createHyperty: createHyperty,
  deleteHyperty: deleteHyperty,
  dataObjectPerName: dataOjectPerName,
  dataObjectPerResources: dataOjectPerResources,
  dataObjectPerUrl: dataObjectPerUrl,
  hypertyPerUser: hypertyPerUser,
  hypertyResourcesSchemes: hypertyResourcesSchemes
};
