'use strict';

const expect = require('chai').expect;
const sinon = require('sinon');

const hyperty = require('../src/dataObject');
const RequestShim = require('../src/js-request');

describe('Hyperty operations', function() {

  it('performs READ (per user) operation correctly', (done) => {

    let request = new RequestShim();
    let requestStub = sinon.stub(request);

    let data = {
      "type": "read",
      "from": "hyperty://hybroker.rethink.ptinovacao.pt/ff517bb0-832e-4326-883a-4d48c5e1afae",
      "to": "domain://registry.hybroker.rethink.ptinovacao.pt/",
      "body": {
        "resource": "user://gmail.com/openidtest10"
      },
      "id": 5
    };

    let response = {
      "hyperty://hybroker.rethink.ptinovacao.pt/24a0724a-68ff-4f0d-ba2b-1e71911a7213": {
        "resources": ["chat"],
        "dataSchemes": ["comm"],
        "startingTime": "2016-07-26T13:39:57Z",
        "hypertyID": "hyperty://hybroker.rethink.ptinovacao.pt/24a0724a-68ff-4f0d-ba2b-1e71911a7213",
        "userID": "user://gmail.com/openidtest10",
        "lastModified": "2016-07-26T13:39:57Z",
        "expires": 3600
      }
    };

    let expectedUrl = 'test://domain/hyperty/user/' + encodeURIComponent(data.body.resource);

    requestStub.get.callsArgWith(1, null, JSON.stringify(response), 200);

    hyperty.read(data.body, requestStub, 'test://domain', false, (value) => {
      expect(value.code).to.equals(200);
      expect(value.value).to.deep.equal(response);
      expect(requestStub.get.calledOnce).to.be.true;
      expect(requestStub.get.calledWith(expectedUrl))

      done();
    });

  });

  it('performs READ (per resources/dataschemes) operation correctly', (done) => {

    let request = new RequestShim();
    let requestStub = sinon.stub(request);

    let data = {
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

    let response = {
        "hyperty://hybroker.rethink.ptinovacao.pt/24a0724a-68ff-4f0d-ba2b-1e71911a7213": {
            "resources": [
                  "chat"
                ],
            "dataSchemes": [
                  "comm"
                ],
            "startingTime": "2016-07-26T13:39:57Z",
            "hypertyID": "hyperty://hybroker.rethink.ptinovacao.pt/24a0724a-68ff-4f0d-ba2b-1e71911a7213",
            "userID": "user://gmail.com/openidtest10",
            "lastModified": "2016-07-26T13:39:57Z",
            "expires": 3600
          }
    };

    let expectedUrl = 'test://domain/hyperty/user/' + encodeURIComponent(data.body.resource)
                    + '/hyperty?resources=chat&dataSchemes=comm';

    requestStub.get.callsArgWith(1, null, JSON.stringify(response), 200);

    hyperty.read(data.body, requestStub, 'test://domain', false, (value) => {
      expect(value.code).to.equals(200);
      expect(value.value).to.deep.equal(response);
      expect(requestStub.get.calledOnce).to.be.true;
      expect(requestStub.get.calledWith(expectedUrl))

      done();
    });

  });

  it('performs UPDATE operation correctly', (done) => {

    let request = new RequestShim();
    let requestStub = sinon.stub(request);

    let data = {
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

    let response  = {
      "message": "Hyperty created"
    };

    let expectedUrl = 'test://domain/hyperty/user/' + encodeURIComponent(data.body.value.hypertyURL);

    requestStub.put.callsArgWith(2, null, JSON.stringify(response), 200);

    hyperty.create(data.body, requestStub, 'test://domain', (value) => {
      expect(value.code).to.equals(200);
      expect(requestStub.put.calledOnce).to.be.true;
      expect(requestStub.put.calledWith(expectedUrl));

      done();
    });
  });

  it('performs CREATE operation correctly', (done) => {

    let request = new RequestShim();
    let requestStub = sinon.stub(request);

    let data = {
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

    let response  = {
      "message": "Hyperty created"
    };

    let expectedUrl = 'test://domain/hyperty/user/' + encodeURIComponent(data.body.value.hypertyURL);

    requestStub.put.callsArgWith(2, null, JSON.stringify(response), 200);

    hyperty.create(data.body, requestStub, 'test://domain', (value) => {
      expect(value.code).to.equals(200);
      expect(requestStub.put.calledOnce).to.be.true;
      expect(requestStub.put.calledWith(expectedUrl));

      done();
    });
  });

  it('performs DELETE operation correctly', (done) => {

    let request = new RequestShim();
    let requestStub = sinon.stub(request);

    let data = {
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

    let response  = {
      "message": "Hyperty deleted"
    };

    let expectedUrl = 'test://domain/hyperty/user/'
      + encodeURIComponent(data.body.value.user) + '/' + encodeURIComponent(data.body.value.hypertyURL);

    requestStub.del.callsArgWith(1, null, JSON.stringify(response), 200);

    hyperty.del(data.body, requestStub, 'test://domain', (value) => {
      expect(value.code).to.equals(200);
      expect(requestStub.del.calledOnce).to.be.true;
      expect(requestStub.del.calledWith(expectedUrl));

      done();
    });

  });

});
