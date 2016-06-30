'use strict';

const expect = require('chai').expect;
const RegistryConnector = require('../RegistryConnector.js');
const sinon = require('sinon');

describe('Add Data Object', function() {

  it('returns data successfully', (done) => {

    let connector = new RegistryConnector('http://test.com');

    let putStub = sinon.stub(connector._request, 'put');

    let input = {
      name: 'user1-do',
      schema: 'do-schema',
      expires: 9000,
      url: 'do-url',
      reporter: 'do-reporter'
    };

    let sentData = {
      'name': input.name,
      'schema': input.schema,
      'url': input.url,
      'reporter': input.reporter,
      'expires': input.expires
    };

    putStub.callsArgWith(2, null, "", 200);

    connector.addDataObject(input.name, input.schema, input.expires, input.url, input.reporter, (result) => {
      expect(result.code).to.equals(200);
      expect(putStub.calledOnce).to.be.true;
      expect(putStub.calledWith('http://test.com/hyperty/dataobject/do-url',
                                JSON.stringify(sentData))).to.be.true;

      done();
    });

  });

});
