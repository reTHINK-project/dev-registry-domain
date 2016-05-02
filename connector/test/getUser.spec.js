'use strict';

const expect = require('chai').expect;
const RegistryConnector = require('../RegistryConnector.js');
const sinon = require('sinon');

describe('Get User ', function() {

  it('returns data successfully', (done) => {

    let connector = new RegistryConnector('http://test');

    let getStub = sinon.stub(connector._request, 'get');

    let userid = 'user1';

    let data = {data: 'data'};

    getStub.callsArgWith(1, null, JSON.stringify(data), 200);

    connector.getUser(userid, (value) => {
      expect(value.code).to.equals(200);
      expect(value.value).to.deep.equal(data);
      expect(getStub.calledOnce).to.be.true;
      expect(getStub.calledWith('http://test/hyperty/user/user1')).to.be.true;

      done();
    });

  });

});
