'use strict';

const expect = require('chai').expect;
const sinon = require('sinon');

const dataObject = require('../src/dataObject');
const RequestShim = require('../src/js-request');

describe('Data Object operations', function() {

  it('performs READ (by name) operation correctly', (done) => {

    let request = new RequestShim();
    let requestStub = sinon.stub(request);

    let data = {
      "type": "read",
      "from": "hyperty://hybroker.rethink.ptinovacao.pt/ff517bb0-832e-4326-883a-4d48c5e1afae",
      "to": "domain://registry.hybroker.rethink.ptinovacao.pt/",
      "body": {
        "resource": "myChat"
      },
      "id": 5
    };

    let response = {
      "comm://hybroker.rethink.ptinovacao.pt/aa2f5bec-e3f7-471f-8ace-44c64edb8a6d": {
        "schema": "hyperty-catalogue://catalogue.hybroker.rethink.ptinovacao.pt/.well-known/dataschema/Communication",
        "url": "comm://hybroker.rethink.ptinovacao.pt/aa2f5bec-e3f7-471f-8ace-44c64edb8a6d",
        "reporter": "hyperty://hybroker.rethink.ptinovacao.pt/24a0724a-68ff-4f0d-ba2b-1e71911a7213",
        "name": "myChat",
        "startingTime": "2016-07-26T12:54:37Z",
        "lastModified": "2016-07-26T13:03:58Z"
      }
    };

    requestStub.get.callsArgWith(1, null, JSON.stringify(response), 200);

    dataObject.read(data.body, requestStub, 'test://domain', false, (value) => {
      expect(value.code).to.equals(200);
      expect(value.value).to.deep.equal(response);
      expect(requestStub.get.calledOnce).to.be.true;
      expect(requestStub.get.calledWith('test://domain/hyperty/dataobject/name/myChat'))

      done();
    });

  });

  it('performs READ (by URL) operation correctly', (done) => {

    let request = new RequestShim();
    let requestStub = sinon.stub(request);

    let data = {
      "type": "read",
      "from": "hyperty://hybroker.rethink.ptinovacao.pt/ff517bb0-832e-4326-883a-4d48c5e1afae",
      "to": "domain://registry.hybroker.rethink.ptinovacao.pt/",
      "body": {
        "resource": "comm://hybroker.rethink.ptinovacao.pt/aa2f5bec-e3f7-471f-8ace-44c64edb8a6d"
      },
      "id": 5
    };

    let response  = {
        "schema": "hyperty-catalogue://catalogue.hybroker.rethink.ptinovacao.pt/.well-known/dataschema/Communication",
        "url": "comm://hybroker.rethink.ptinovacao.pt/aa2f5bec-e3f7-471f-8ace-44c64edb8a6d",
        "reporter": "hyperty://hybroker.rethink.ptinovacao.pt/24a0724a-68ff-4f0d-ba2b-1e71911a7213",
        "name": "myChat",
        "startingTime": "2016-07-26T10:43:03Z",
        "lastModified": "2016-07-26T10:43:03Z"
    };

    requestStub.get.callsArgWith(1, null, JSON.stringify(response), 200);

    dataObject.read(data.body, requestStub, 'test://domain', false, (value) => {
      expect(value.code).to.equals(200);
      expect(value.value).to.deep.equal(response);
      expect(requestStub.get.calledOnce).to.be.true;
      expect(requestStub.get.calledWith('test://domain/hyperty/dataobject/url/' + encodeURIComponent(data.body.resource)))

      done();
    });

  });

  it('performs READ (advanced/by resources) operation correctly', (done) => {

    let request = new RequestShim();
    let requestStub = sinon.stub(request);

    let data = {
      "type": "read",
      "from": "hyperty://hybroker.rethink.ptinovacao.pt/ff517bb0-832e-4326-883a-4d48c5e1afae",
      "to": "domain://registry.hybroker.rethink.ptinovacao.pt/",
      "body": {
        "resource": "myChat",
        "criteria": {
          "resources": ["chat"]
        }
      },
      "id": 5
    };

    let response  = {
      "comm://hybroker.rethink.ptinovacao.pt/aa2f5bec-e3f7-471f-8ace-44c64edb8a6d": {
        "schema": "hyperty-catalogue://catalogue.hybroker.rethink.ptinovacao.pt/.well-known/dataschema/Communication",
        "url": "comm://hybroker.rethink.ptinovacao.pt/aa2f5bec-e3f7-471f-8ace-44c64edb8a6d",
        "reporter": "hyperty://hybroker.rethink.ptinovacao.pt/24a0724a-68ff-4f0d-ba2b-1e71911a7213",
        "name": "myChat",
        "startingTime": "2016-07-26T12:54:37Z",
        "resources": [
          "chat"
        ],
        "lastModified": "2016-07-26T12:54:37Z"
      }
    };

    let expectedUrl = 'test://domain/hyperty/dataobject/name/' + encodeURIComponent(data.body.resource)
                    + '/do?resources=chat';

    requestStub.get.callsArgWith(1, null, JSON.stringify(response), 200);

    dataObject.read(data.body, requestStub, 'test://domain', false, (value) => {
      expect(value.code).to.equals(200);
      expect(value.value).to.deep.equal(response);
      expect(requestStub.get.calledOnce).to.be.true;
      expect(requestStub.get.calledWith(expectedUrl));

      done();
    });

  });

  it('performs CREATE operation correctly', (done) => {

    let request = new RequestShim();
    let requestStub = sinon.stub(request);

    let data = {
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
          "resources": ["chat"],
          "reporter": "hyperty://hybroker.rethink.ptinovacao.pt/24a0724a-68ff-4f0d-ba2b-1e71911a7213",
          "preAuth": [],
          "subscribers": []
        },
        "policy": "policy"
      }
    };

    let response  = {
        "message": "Data object created"
    };

    let expectedUrl = 'test://domain/hyperty/dataobject/' + encodeURIComponent(data.body.value.url);

    requestStub.put.callsArgWith(2, null, JSON.stringify(response), 200);

    dataObject.create(data.body, requestStub, 'test://domain', (value) => {
      expect(value.code).to.equals(200);
      expect(requestStub.put.calledOnce).to.be.true;
      expect(requestStub.put.calledWith(expectedUrl));

      done();
    });
  });

  it('performs UPDATE operation correctly', (done) => {

    let request = new RequestShim();
    let requestStub = sinon.stub(request);

    let data = {
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
          "resources": ["chat"],
          "reporter": "hyperty://hybroker.rethink.ptinovacao.pt/24a0724a-68ff-4f0d-ba2b-1e71911a7213",
          "preAuth": [],
          "subscribers": []
        },
        "policy": "policy"
      }
    };

    let response  = {
        "message": "Data object created"
    };

    let expectedUrl = 'test://domain/hyperty/dataobject/' + encodeURIComponent(data.body.value.url);

    requestStub.put.callsArgWith(2, null, JSON.stringify(response), 200);

    dataObject.create(data.body, requestStub, 'test://domain', (value) => {
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
          "name": "comm://hybroker.rethink.ptinovacao.pt/aa2f5bec-e3f7-471f-8ace-44c64edb8a6d"
        }
      },
      "id": 8
    };

    let response  = {
        "message": "Data object deleted"
    };

    let expectedUrl = 'test://domain/hyperty/dataobject/url' + encodeURIComponent(data.body.value.url);

    requestStub.del.callsArgWith(1, null, JSON.stringify(response), 200);

    dataObject.del(data.body, requestStub, 'test://domain', (value) => {
      expect(value.code).to.equals(200);
      expect(requestStub.del.calledOnce).to.be.true;
      expect(requestStub.del.calledWith(expectedUrl));

      done();
    });

  });

});
