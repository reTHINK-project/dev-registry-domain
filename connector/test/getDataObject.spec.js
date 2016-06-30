'use strict';

const expect = require('chai').expect;
const RegistryConnector = require('../RegistryConnector.js');
const sinon = require('sinon');

describe('Get Data Object', function() {

  it('returns data successfully', (done) => {

    let connector = new RegistryConnector('http://test.com');

    let getStub = sinon.stub(connector._request, 'get');

    let resource = 'dataObject://user1-do';
    let encodedResource = encodeURIComponent(resource);

    let data = {data: 'data'};

    getStub.callsArgWith(1, null, JSON.stringify(data), 200);

    connector.getDataObject(resource, (value) => {
      expect(value.code).to.equals(200);
      expect(value.value).to.deep.equal(data);
      expect(getStub.calledOnce).to.be.true;
      expect(getStub.calledWith('http://test.com/hyperty/dataobject/url/' + encodedResource)).to.be.true;

      done();
    });

  });

});
