'use strict';

const expect = require('chai').expect;
const RegistryConnector = require('../RegistryConnector.js');
const sinon = require('sinon');

describe('Process message method', function() {

  it('READ operation - Hyperty', (done) => {

    let connector = new RegistryConnector('http://test');

    let doStub = sinon.stub(connector, 'getDataObject');
    let hypertyStub = sinon.stub(connector, 'getUser');

    let callback = function() { done(); };

    let message = {
      type: 'READ',
      body: {
        resource: 'hyperty://test1-test1'
      }
    };


    hypertyStub.callsArg(1);

    connector.processMessage(message, callback);

    expect(hypertyStub.calledOnce).to.be.true;
    expect(doStub.calledOnce).to.be.false;
    expect(hypertyStub.calledWith(message.body.resource, callback)).to.be.true;

  });

  it('READ operation - Data Object', (done) => {

    let connector = new RegistryConnector('http://test');

    let doStub = sinon.stub(connector, 'getDataObject');
    let hypertyStub = sinon.stub(connector, 'getUser');

    let callback = function() { done(); };

    let message = {
      type: 'READ',
      body: {
        resource: 'dataObject://test'
      }
    };


    doStub.callsArg(1);

    connector.processMessage(message, callback);

    expect(doStub.calledOnce).to.be.true;
    expect(hypertyStub.calledOnce).to.be.false;
    expect(doStub.calledWith(message.body.resource, callback)).to.be.true;

  });

  it('CREATE operation - Hyperty', (done) => {

    let connector = new RegistryConnector('http://test');

    let doStub = sinon.stub(connector, 'addDataObject');
    let hypertyStub = sinon.stub(connector, 'addHyperty');

    let callback = function() { done(); };

    let message = {
      type: 'CREATE',
      body: {
        value: {
          user: 'user1',
          hypertyURL: 'hyperty://testest1',
          hypertyDescriptorURL: 'hyperty-descriptor://testtest',
          expires: 19000
        }
      }
    };


    hypertyStub.callsArg(4);

    connector.processMessage(message, callback);

    expect(hypertyStub.calledOnce).to.be.true;
    expect(doStub.calledOnce).to.be.false;
    expect(hypertyStub.calledWith(message.body.value.user, message.body.value.hypertyURL,
                                  message.body.value.hypertyDescriptorURL, message.body.value.expires,
                                  callback)).to.be.true;

  });

  it('CREATE operation - Data Object', (done) => {

    let connector = new RegistryConnector('http://test');

    let doStub = sinon.stub(connector, 'addDataObject');
    let hypertyStub = sinon.stub(connector, 'addHyperty');

    let callback = function() { done(); };

    let message = {
      type: 'CREATE',
      body: {
        value: {
          name: 'user1-do',
          schema: 'doschema',
          expires: 1900,
          url: 'dataObject://123456',
          reporter: 'do-reporter'
        }
      }
    };


    doStub.callsArg(5);

    connector.processMessage(message, callback);

    expect(doStub.calledOnce).to.be.true;
    expect(hypertyStub.calledOnce).to.be.false;
    expect(doStub.calledWith(message.body.value.name, message.body.value.schema,
                                  message.body.value.expires, message.body.value.url,
                                  message.body.value.reporter,
                                  callback)).to.be.true;

  });

  it('UPDATE operation - Hyperty', (done) => {

    let connector = new RegistryConnector('http://test');

    let doStub = sinon.stub(connector, 'addDataObject');
    let hypertyStub = sinon.stub(connector, 'addHyperty');

    let callback = function() { done(); };

    let message = {
      type: 'UPDATE',
      body: {
        value: {
          user: 'user1',
          hypertyURL: 'hyperty://testest1',
          hypertyDescriptorURL: 'hyperty-descriptor://testtest',
          expires: 19000
        }
      }
    };


    hypertyStub.callsArg(4);

    connector.processMessage(message, callback);

    expect(hypertyStub.calledOnce).to.be.true;
    expect(doStub.calledOnce).to.be.false;
    expect(hypertyStub.calledWith(message.body.value.user, message.body.value.hypertyURL,
                                  message.body.value.hypertyDescriptorURL, message.body.value.expires,
                                  callback)).to.be.true;

  });

  it('UPDATE operation - Data Object', (done) => {

    let connector = new RegistryConnector('http://test');

    let doStub = sinon.stub(connector, 'addDataObject');
    let hypertyStub = sinon.stub(connector, 'addHyperty');

    let callback = function() { done(); };

    let message = {
      type: 'UPDATE',
      body: {
        value: {
          name: 'user1-do',
          schema: 'doschema',
          expires: 1900,
          url: 'dataObject://123456',
          reporter: 'do-reporter'
        }
      }
    };


    doStub.callsArg(5);

    connector.processMessage(message, callback);

    expect(doStub.calledOnce).to.be.true;
    expect(hypertyStub.calledOnce).to.be.false;
    expect(doStub.calledWith(message.body.value.name, message.body.value.schema,
                                  message.body.value.expires, message.body.value.url,
                                  message.body.value.reporter,
                                  callback)).to.be.true;

  });

  it('DELETE operation - Hyperty', (done) => {

    let connector = new RegistryConnector('http://test');

    let doStub = sinon.stub(connector, 'deleteDataObject');
    let hypertyStub = sinon.stub(connector, 'deleteHyperty');

    let callback = function() { done(); };

    let message = {
      type: 'DELETE',
      body: {
        value: {
          user: 'user1',
          hypertyURL: 'hyperty://testest1',
        }
      }
    };


    hypertyStub.callsArg(2);

    connector.processMessage(message, callback);

    expect(hypertyStub.calledOnce).to.be.true;
    expect(doStub.calledOnce).to.be.false;
    expect(hypertyStub.calledWith(message.body.value.user, message.body.value.hypertyURL, callback)).to.be.true;

  });

  it('DELETE operation - Data Object', (done) => {

    let connector = new RegistryConnector('http://test');

    let doStub = sinon.stub(connector, 'deleteDataObject');
    let hypertyStub = sinon.stub(connector, 'deleteHyperty');

    let callback = function() { done(); };

    let message = {
      type: 'DELETE',
      body: {
        value: {
          name: 'user1-do',
        }
      }
    };


    doStub.callsArg(1);

    connector.processMessage(message, callback);

    expect(doStub.calledOnce).to.be.true;
    expect(hypertyStub.calledOnce).to.be.false;
    expect(doStub.calledWith(message.body.value.name, callback)).to.be.true;

  });

});
