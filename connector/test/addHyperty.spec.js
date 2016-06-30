'use strict';

const expect = require('chai').expect;
const RegistryConnector = require('../RegistryConnector.js');
const sinon = require('sinon');

describe('Add Hyperty ', function() {

  it('returns data successfully', (done) => {

    let connector = new RegistryConnector('http://test.com');

    let putStub = sinon.stub(connector._request, 'put');

    let input = {
      userid: 'user1',
      hypertyid: 'hyperty1',
      hypertyDescriptor: 'hyperty-descriptor',
      expires: 9000,
      resources: ['resource1', 'resource2'],
      dataschemes: ['scheme1']
    };

    let sentData = {
      'descriptor': input.hypertyDescriptor,
      'expires': input.expires,
      'resources': ['resource1', 'resource2'],
      'dataSchemes': ['scheme1']
    };

    putStub.callsArgWith(2, null, "", 200);

    connector.addHyperty(input.userid, input.hypertyid, input.hypertyDescriptor, input.expires, input.resources, input.dataschemes, (result) => {
      expect(result.code).to.equals(200);
      expect(putStub.calledOnce).to.be.true;
      expect(putStub.calledWith('http://test.com/hyperty/user/user1/hyperty1',
                                JSON.stringify(sentData))).to.be.true;

      done();
    });

  });

});
