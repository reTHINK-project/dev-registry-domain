'use strict';

const expect = require('chai').expect;
const RegistryConnector = require('../RegistryConnector.js');
const sinon = require('sinon');

describe('Delete Hyperty', function() {

  it('returns data successfully', (done) => {

    let connector = new RegistryConnector('http://test');

    let delStub = sinon.stub(connector._request, 'del');

    let userID = 'user1';
    let hypertyID = 'hyperty-user1';

    delStub.callsArgWith(1, null, "", 200);

    connector.deleteHyperty(userID, hypertyID, (result) => {
      expect(result.code).to.equals(200);
      expect(delStub.calledOnce).to.be.true;
      expect(delStub.calledWith('http://test/hyperty/user/user1/hyperty-user1')).to.be.true;

      done();
    });

  });

});
