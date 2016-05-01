'use strict';

const expect = require('chai').expect;
const RegistryConnector = require('../RegistryConnector.js');
const sinon = require('sinon');

describe('Delete Data Object', function() {

  it('returns data successfully', (done) => {

    let connector = new RegistryConnector('http://test');

    let delStub = sinon.stub(connector._request, 'del');

    let doName = 'user1-do';

    delStub.callsArgWith(1, null, "", 200);

    connector.deleteDataObject(doName, (result) => {
      expect(result.code).to.equals(200);
      expect(delStub.calledOnce).to.be.true;
      expect(delStub.calledWith('http://test/hyperty/dataobject/user1-do')).to.be.true;

      done();
    });

  });

});
