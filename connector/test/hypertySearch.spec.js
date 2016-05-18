'use strict';

const expect = require('chai').expect;
const RegistryConnector = require('../RegistryConnector.js');
const sinon = require('sinon');

describe('Hyperty Search', function() {

  it('with resources and dataschemes', (done) => {

    let connector = new RegistryConnector('http://test');

    let getStub = sinon.stub(connector._request, 'get');

    let resources = ['comm', 'voice'];
    let dataschemes = ['ds1', 'ds2'];
    let userid = 'user1'

    let expectedURL = 'http://test/hyperty/user/user1/hyperty?resources=comm,voice&dataSchemes=ds1,ds2';

    let data = {data: 'data'};

    getStub.callsArgWith(1, null, JSON.stringify(data), 200);

    connector.hypertySearch(userid, resources, dataschemes, (value) => {
      expect(value.code).to.equals(200);
      expect(value.value).to.deep.equal(data);
      expect(getStub.calledOnce).to.be.true;
      expect(getStub.calledWith(expectedURL)).to.be.true;

      done();
    });

  });

  it('with resources only', (done) => {

    let connector = new RegistryConnector('http://test');

    let getStub = sinon.stub(connector._request, 'get');

    let resources = ['comm', 'voice'];
    let dataschemes = [];
    let userid = 'user1'

    let expectedURL = 'http://test/hyperty/user/user1/hyperty?resources=comm,voice';

    let data = {data: 'data'};

    getStub.callsArgWith(1, null, JSON.stringify(data), 200);

    connector.hypertySearch(userid, resources, dataschemes, (value) => {
      expect(value.code).to.equals(200);
      expect(value.value).to.deep.equal(data);
      expect(getStub.calledOnce).to.be.true;
      expect(getStub.calledWith(expectedURL)).to.be.true;

      done();
    });

  });

  it('with dataschemes only', (done) => {

    let connector = new RegistryConnector('http://test');

    let getStub = sinon.stub(connector._request, 'get');

    let resources = [];
    let dataschemes = ['ds1', 'ds2'];
    let userid = 'user1'

    let expectedURL = 'http://test/hyperty/user/user1/hyperty?dataSchemes=ds1,ds2';

    let data = {data: 'data'};

    getStub.callsArgWith(1, null, JSON.stringify(data), 200);

    connector.hypertySearch(userid, resources, dataschemes, (value) => {
      expect(value.code).to.equals(200);
      expect(value.value).to.deep.equal(data);
      expect(getStub.calledOnce).to.be.true;
      expect(getStub.calledWith(expectedURL)).to.be.true;

      done();
    });

  });

});
