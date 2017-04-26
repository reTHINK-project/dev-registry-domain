const expect = require('chai').expect;
const sinon = require('sinon');
const nock = require('nock');

const Hyperty = require('../src/hyperty');
const Request = require('../src/js-request');

const DOMAIN_URL = 'http://fakedomain.dev';
const request = new Request({enabled: false});
const hyperty = new Hyperty(request, DOMAIN_URL);

describe('Hyperty operations', function() {

  it('READ - hyperties by user - hyperties returned', (done) => {

    const message = {
      "type": "read",
      "from": "hyperty://hybroker.rethink.ptinovacao.pt/ff517bb0-832e-4326-883a-4d48c5e1afae",
      "to": "domain://registry.hybroker.rethink.ptinovacao.pt/",
      "body": {
        "resource": "user://google.com/openIdTest10"
      },
      "id": 5
    };

    const response = {
      "hyperty://inesc-id.pt/b7b3rs4-3245-42gn-4127-238jhdq83d8": {
        "resources" : [
          "video",
        ],
        "dataSchemes" : [
          "comm"
        ],
        "descriptor": "hyperty-catalogue://localhost/HelloHyperty",
        "startingTime": "2016-02-08T13:42:00Z",
        "hypertyID": "hyperty://localhost/075932a5-7ef3-40dd-bcc4-34094ab907e7",
        "userID": "user://inesc-id.pt/rui",
        "lastModified": "2016-02-08T13:42:53Z",
        "status": "live",
        "expires": 1211,
        "guid": "guid",
        "runtime": "runtime",
        "p2pRequester": "p2pRequester",
        "p2pHandler": "p2pHandler"
      }
    };

    const scope =
      nock(DOMAIN_URL)
      .get(`/hyperty/user/${encodeURIComponent(message.body.resource)}`)
      .reply(200, response);

    hyperty.read(message.body, false, (reply) => {
      expect(reply.code).to.equal(200);
      expect(reply.value).equal(JSON.stringify(response));
      done();
    });

  });

  it('READ - hyperties by user - no active hyperties', (done) => {

    const message = {
      "type": "read",
      "from": "hyperty://hybroker.rethink.ptinovacao.pt/ff517bb0-832e-4326-883a-4d48c5e1afae",
      "to": "domain://registry.hybroker.rethink.ptinovacao.pt/",
      "body": {
        "resource": "user://google.com/doesNotExist"
      },
      "id": 5 };

    const response = {
      "hyperty://inesc-id.pt/b7b3rs4-3245-42gn-4127-238jhdq83d8": {
        "resources" : [
          "video",
        ],
        "dataSchemes" : [
          "comm"
        ],
        "descriptor": "hyperty-catalogue://localhost/HelloHyperty",
        "startingTime": "2016-02-08T13:42:00Z",
        "hypertyID": "hyperty://localhost/075932a5-7ef3-40dd-bcc4-34094ab907e7",
        "userID": "user://inesc-id.pt/rui",
        "lastModified": "2016-02-08T13:42:53Z",
        "status": "disconnected",
        "expires": 1211,
        "guid": "guid",
        "runtime": "runtime",
        "p2pRequester": "p2pRequester",
        "p2pHandler": "p2pHandler"
      }
    };

    const scope =
      nock(DOMAIN_URL)
      .get(`/hyperty/user/${encodeURIComponent(message.body.resource)}`)
      .reply(408, response);

    hyperty.read(message.body, false, (reply) => {
      expect(reply.code).to.equal(408);
      expect(reply.value).equal(JSON.stringify(response));
      expect(reply.description).equal('Temporarily Unavailable');
      done();
    });
  });

  it('READ - hyperties by user - non-existent user', (done) => {

    const message = {
      "type": "read",
      "from": "hyperty://hybroker.rethink.ptinovacao.pt/ff517bb0-832e-4326-883a-4d48c5e1afae",
      "to": "domain://registry.hybroker.rethink.ptinovacao.pt/",
      "body": {
        "resource": "user://google.com/doesNotExist"
      },
      "id": 5
    };

    const response = {
      message: "Not found"
    };

    const scope =
      nock(DOMAIN_URL)
      .get(`/hyperty/user/${encodeURIComponent(message.body.resource)}`)
      .reply(404, response);

    hyperty.read(message.body, false, (reply) => {
      expect(reply.code).to.equal(404);
      expect(reply.description).equal(response.message);
      done();
    });

  });

  it('READ - hyperties by guid', (done) => {
    const message = {
      "type": "read",
      "from": "hyperty://hybroker.rethink.ptinovacao.pt/ff517bb0-832e-4326-883a-4d48c5e1afae",
      "to": "domain://registry.hybroker.rethink.ptinovacao.pt/",
      "body": {
        "resource": "user-guid://testguid"
      },
      "id": 5
    };

    const response = {
      "hyperty://inesc-id.pt/b7b3rs4-3245-42gn-4127-238jhdq83d8": {
        "resources" : [
          "video",
        ],
        "dataSchemes" : [
          "comm"
        ],
        "descriptor": "hyperty-catalogue://localhost/HelloHyperty",
        "startingTime": "2016-02-08T13:42:00Z",
        "hypertyID": "hyperty://localhost/075932a5-7ef3-40dd-bcc4-34094ab907e7",
        "userID": "user://inesc-id.pt/rui",
        "lastModified": "2016-02-08T13:42:53Z",
        "status": "live",
        "expires": 1211,
        "guid": "user-guid://testguid",
        "runtime": "runtime",
        "p2pRequester": "p2pRequester",
        "p2pHandler": "p2pHandler"
      }
    };

    const scope =
      nock(DOMAIN_URL)
      .get(`/hyperty/guid/${encodeURIComponent(message.body.resource)}`)
      .reply(200, response);

    hyperty.read(message.body, false, (reply) => {
      expect(reply.code).to.equal(200);
      expect(reply.value).equal(JSON.stringify(response));
      done();
    });
  });

  it('READ - hyperties by email - simple', (done) => {
    const message = {
      "type":"read",
      "from":"runtime://localhost/2ae0f318-361f-1127-c61d-643c63876800/discovery/",
      "to":"domain://registry.localhost/",
      "body":{
        "resource":"/hyperty/idp-identifier/test@gmail.com",
        "auth":false,
        "via":"runtime://localhost/protostub/d5de65da-edac-e8a6-9415-a969d6c14e19"
      },
      "id":11
    };

    const response = {
      "hyperty://inesc-id.pt/b7b3rs4-3245-42gn-4127-238jhdq83d8": {
        "resources" : [
          "video",
        ],
        "dataSchemes" : [
          "comm"
        ],
        "descriptor": "hyperty-catalogue://localhost/HelloHyperty",
        "startingTime": "2016-02-08T13:42:00Z",
        "hypertyID": "hyperty://localhost/075932a5-7ef3-40dd-bcc4-34094ab907e7",
        "userID": "user://inesc-id.pt/rui",
        "lastModified": "2016-02-08T13:42:53Z",
        "status": "live",
        "expires": 1211,
        "guid": "user-guid://testguid",
        "runtime": "runtime",
        "p2pRequester": "p2pRequester",
        "p2pHandler": "p2pHandler"
      }
    };

    const scope =
      nock(DOMAIN_URL)
      .get(`/hyperty/email/${encodeURIComponent("test@gmail.com")}`)
      .reply(200, response);

    hyperty.read(message.body, false, (reply) => {
      expect(reply.code).to.equal(200);
      expect(reply.value).equal(JSON.stringify(response));
      done();
    });
  });

  it('READ - hyperties by url - simple', (done) => {
    const message = {
      "type": "read",
      "from": "hyperty://hybroker.rethink.ptinovacao.pt/ff517bb0-832e-4326-883a-4d48c5e1afae",
      "to": "domain://registry.hybroker.rethink.ptinovacao.pt/",
      "body": {
        "resource": "hyperty://hybroker.rethink.ptinovacao.pt/11111172-ff57-4b5d-a365-ccc1d5c11d10"
      },
      "id": 5
    };

    const response = {
      "resources" : [
        "resource1",
        "resource2"
      ],
      "dataSchemes" : [
        "comm"
      ],
      "descriptor": "hyperty-catalogue://localhost/HelloHyperty",
      "startingTime": "2016-02-08T13:42:00Z",
      "hypertyID": "hyperty://hybroker.rethink.ptinovacao.pt/11111172-ff57-4b5d-a365-ccc1d5c11d10",
      "userID": "user://google.com/openIdTest10",
      "lastModified": "2016-02-08T13:42:53Z",
      "status": "live",
      "expires": 1211,
      "guid": "guid",
      "runtime": "runtime",
      "p2pRequester": "p2pRequester",
      "p2pHandler": "p2pHandler"
    };

    const scope =
      nock(DOMAIN_URL)
      .get(`/hyperty/url/${encodeURIComponent(message.body.resource)}`)
      .reply(200, response);

    hyperty.read(message.body, false, (reply) => {
      expect(reply.code).to.equal(200);
      expect(reply.value).equal(JSON.stringify(response));
      done();
    });
  });

  it('READ - hyperties by user - advanced (resources)', (done) => {
    const message = {
      "type": "read",
      "from": "hyperty://hybroker.rethink.ptinovacao.pt/ff517bb0-832e-4326-883a-4d48c5e1afae",
      "to": "domain://registry.hybroker.rethink.ptinovacao.pt/",
      "body": {
        "resource": "user://google.com/openIdTest10",
        "criteria": {
          "resources": ["resource1", "resource2"]
        }
      },
      "id": 5
    };

    const response = {
      "hyperty://inesc-id.pt/b7b3rs4-3245-42gn-4127-238jhdq83d8": {
        "resources" : [
          "resource1",
          "resource2"
        ],
        "dataSchemes" : [
          "comm"
        ],
        "descriptor": "hyperty-catalogue://localhost/HelloHyperty",
        "startingTime": "2016-02-08T13:42:00Z",
        "hypertyID": "hyperty://localhost/075932a5-7ef3-40dd-bcc4-34094ab907e7",
        "userID": "user://google.com/openIdTest10",
        "lastModified": "2016-02-08T13:42:53Z",
        "status": "live",
        "expires": 1211,
        "guid": "guid",
        "runtime": "runtime",
        "p2pRequester": "p2pRequester",
        "p2pHandler": "p2pHandler"
      }
    };

    const scope =
      nock(DOMAIN_URL)
      .get(`/hyperty/user/${encodeURIComponent(message.body.resource)}/hyperty?resources=${message.body.criteria.resources.join(',')}`)
      .reply(200, response);

    hyperty.read(message.body, true, (reply) => {
      expect(reply.code).to.equal(200);
      expect(reply.value).equal(JSON.stringify(response));
      done();
    });
  });

  it('READ - hyperties by user - advanced (dataschemes)', (done) => {
    const message = {
      "type": "read",
      "from": "hyperty://hybroker.rethink.ptinovacao.pt/ff517bb0-832e-4326-883a-4d48c5e1afae",
      "to": "domain://registry.hybroker.rethink.ptinovacao.pt/",
      "body": {
        "resource": "user://google.com/openIdTest10",
        "criteria": {
          "dataSchemes": ["ds1", "ds2"]
        }
      },
      "id": 5
    };

    const response = {
      "hyperty://inesc-id.pt/b7b3rs4-3245-42gn-4127-238jhdq83d8": {
        "resources" : [
          "resource1",
          "resource2"
        ],
        "dataSchemes" : [
          "ds1",
          "ds2"
        ],
        "descriptor": "hyperty-catalogue://localhost/HelloHyperty",
        "startingTime": "2016-02-08T13:42:00Z",
        "hypertyID": "hyperty://localhost/075932a5-7ef3-40dd-bcc4-34094ab907e7",
        "userID": "user://google.com/openIdTest10",
        "lastModified": "2016-02-08T13:42:53Z",
        "status": "live",
        "expires": 1211,
        "guid": "guid",
        "runtime": "runtime",
        "p2pRequester": "p2pRequester",
        "p2pHandler": "p2pHandler"
      }
    };

    const scope =
      nock(DOMAIN_URL)
      .get(`/hyperty/user/${encodeURIComponent(message.body.resource)}/hyperty?dataSchemes=${message.body.criteria.dataSchemes.join(',')}`)
      .reply(200, response);

    hyperty.read(message.body, true, (reply) => {
      expect(reply.code).to.equal(200);
      expect(reply.value).equal(JSON.stringify(response));
      done();
    });
  });

  it('READ - hyperties by user - advanced (resources and dataschemes)', (done) => {
    const message = {
      "type": "read",
      "from": "hyperty://hybroker.rethink.ptinovacao.pt/ff517bb0-832e-4326-883a-4d48c5e1afae",
      "to": "domain://registry.hybroker.rethink.ptinovacao.pt/",
      "body": {
        "resource": "user://google.com/openIdTest10",
        "criteria": {
          "resources": ["resource1", "resource2"],
          "dataSchemes": ["ds1", "ds2"]
        }
      },
      "id": 5
    };

    const response = {
      "hyperty://inesc-id.pt/b7b3rs4-3245-42gn-4127-238jhdq83d8": {
        "resources" : [
          "resource1",
          "resource2"
        ],
        "dataSchemes" : [
          "ds1",
          "ds2"
        ],
        "descriptor": "hyperty-catalogue://localhost/HelloHyperty",
        "startingTime": "2016-02-08T13:42:00Z",
        "hypertyID": "hyperty://localhost/075932a5-7ef3-40dd-bcc4-34094ab907e7",
        "userID": "user://google.com/openIdTest10",
        "lastModified": "2016-02-08T13:42:53Z",
        "status": "live",
        "expires": 1211,
        "guid": "guid",
        "runtime": "runtime",
        "p2pRequester": "p2pRequester",
        "p2pHandler": "p2pHandler"
      }
    };

    const scope =
      nock(DOMAIN_URL)
      .get(`/hyperty/user/${encodeURIComponent(message.body.resource)}/hyperty?resources=${message.body.criteria.resources.join(',')}&dataSchemes=${message.body.criteria.dataSchemes.join(',')}`)
      .reply(200, response);

    hyperty.read(message.body, true, (reply) => {
      expect(reply.code).to.equal(200);
      expect(reply.value).equal(JSON.stringify(response));
      done();
    });
  });

  it('READ - hyperties by user - advanced (none)', (done) => {
    const message = {
      "type": "read",
      "from": "hyperty://hybroker.rethink.ptinovacao.pt/ff517bb0-832e-4326-883a-4d48c5e1afae",
      "to": "domain://registry.hybroker.rethink.ptinovacao.pt/",
      "body": {
        "resource": "user://google.com/openIdTest10",
        "criteria": {}
      },
      "id": 5
    };

    const response = {
      "hyperty://inesc-id.pt/b7b3rs4-3245-42gn-4127-238jhdq83d8": {
        "resources" : [
          "resource1",
          "resource2"
        ],
        "dataSchemes" : [
          "ds1",
          "ds2"
        ],
        "descriptor": "hyperty-catalogue://localhost/HelloHyperty",
        "startingTime": "2016-02-08T13:42:00Z",
        "hypertyID": "hyperty://localhost/075932a5-7ef3-40dd-bcc4-34094ab907e7",
        "userID": "user://google.com/openIdTest10",
        "lastModified": "2016-02-08T13:42:53Z",
        "status": "live",
        "expires": 1211,
        "guid": "guid",
        "runtime": "runtime",
        "p2pRequester": "p2pRequester",
        "p2pHandler": "p2pHandler"
      }
    };

    const scope =
      nock(DOMAIN_URL)
      .get(`/hyperty/user/${encodeURIComponent(message.body.resource)}/hyperty`)
      .reply(200, response);

    hyperty.read(message.body, true, (reply) => {
      expect(reply.code).to.equal(200);
      expect(reply.value).equal(JSON.stringify(response));
      done();
    });
  });

  it('READ - hyperties by email - advanced (resources)', (done) => {
    const message = {
      "type":"read",
      "from":"runtime://localhost/2ae0f318-361f-1127-c61d-643c63876800/discovery/",
      "to":"domain://registry.localhost/",
      "body":{
        "resource":"/hyperty/idp-identifier/test@gmail.com",
        "criteria": {
          "resources": [ "resource1", "resource2"]
        },
        "auth":false,
        "via":"runtime://localhost/protostub/d5de65da-edac-gghj-9415-a969d6c14e19"
      },
      "id":11
    };

    const response = {
      "hyperty://inesc-id.pt/b7b3rs4-3245-42gn-4127-238jhdq83d8": {
        "resources" : [
          "resource1",
          "resource2"
        ],
        "dataSchemes" : [
          "ds1",
          "ds2"
        ],
        "descriptor": "hyperty-catalogue://localhost/HelloHyperty",
        "startingTime": "2016-02-08T13:42:00Z",
        "hypertyID": "hyperty://localhost/075932a5-7ef3-40dd-bcc4-34094ab907e7",
        "userID": "user://google.com/test@gmail.com",
        "lastModified": "2016-02-08T13:42:53Z",
        "status": "live",
        "expires": 1211,
        "guid": "guid",
        "runtime": "runtime",
        "p2pRequester": "p2pRequester",
        "p2pHandler": "p2pHandler"
      }
    };

    const scope =
      nock(DOMAIN_URL)
      .get(`/hyperty/email/${encodeURIComponent("test@gmail.com")}?resources=${message.body.criteria.resources.join(',')}`)
      .reply(200, response);

    hyperty.read(message.body, true, (reply) => {
      expect(reply.code).to.equal(200);
      expect(reply.value).equal(JSON.stringify(response));
      done();
    });
  });

  it('READ - hyperties by email - advanced (dataschemes)', (done) => {
    const message = {
      "type":"read",
      "from":"runtime://localhost/2ae0f318-361f-1127-c61d-643c63876800/discovery/",
      "to":"domain://registry.localhost/",
      "body":{
        "resource":"/hyperty/idp-identifier/test@gmail.com",
        "criteria": {
          "dataSchemes": [ "ds1", "ds2"]
        },
        "auth":false,
        "via":"runtime://localhost/protostub/d5de65da-edac-gghj-9415-a969d6c14e19"
      },
      "id":11
    };

    const response = {
      "hyperty://inesc-id.pt/b7b3rs4-3245-42gn-4127-238jhdq83d8": {
        "resources" : [
          "resource1",
          "resource2"
        ],
        "dataSchemes" : [
          "ds1",
          "ds2"
        ],
        "descriptor": "hyperty-catalogue://localhost/HelloHyperty",
        "startingTime": "2016-02-08T13:42:00Z",
        "hypertyID": "hyperty://localhost/075932a5-7ef3-40dd-bcc4-34094ab907e7",
        "userID": "user://google.com/test@gmail.com",
        "lastModified": "2016-02-08T13:42:53Z",
        "status": "live",
        "expires": 1211,
        "guid": "guid",
        "runtime": "runtime",
        "p2pRequester": "p2pRequester",
        "p2pHandler": "p2pHandler"
      }
    };

    const scope =
      nock(DOMAIN_URL)
      .get(`/hyperty/email/${encodeURIComponent("test@gmail.com")}?dataSchemes=${message.body.criteria.dataSchemes.join(',')}`)
      .reply(200, response);

    hyperty.read(message.body, true, (reply) => {
      expect(reply.code).to.equal(200);
      expect(reply.value).equal(JSON.stringify(response));
      done();
    });
  });

  it('READ - hyperties by email - advanced (resources and dataschemes)', (done) => {
    const message = {
      "type": "read",
      "from": "hyperty://hybroker.rethink.ptinovacao.pt/ff517bb0-832e-4326-883a-4d48c5e1afae",
      "to": "domain://registry.hybroker.rethink.ptinovacao.pt/",
      "body": {
        "resource": "user://google.com/openIdTest10",
        "criteria": {
          "resources": ["resource1", "resource2"],
          "dataSchemes": ["ds1", "ds2"]
        }
      },
      "id": 5
    };

    const response = {
      "hyperty://inesc-id.pt/b7b3rs4-3245-42gn-4127-238jhdq83d8": {
        "resources" : [
          "resource1",
          "resource2"
        ],
        "dataSchemes" : [
          "ds1",
          "ds2"
        ],
        "descriptor": "hyperty-catalogue://localhost/HelloHyperty",
        "startingTime": "2016-02-08T13:42:00Z",
        "hypertyID": "hyperty://localhost/075932a5-7ef3-40dd-bcc4-34094ab907e7",
        "userID": "user://google.com/openIdTest10",
        "lastModified": "2016-02-08T13:42:53Z",
        "status": "live",
        "expires": 1211,
        "guid": "guid",
        "runtime": "runtime",
        "p2pRequester": "p2pRequester",
        "p2pHandler": "p2pHandler"
      }
    };

    const scope =
      nock(DOMAIN_URL)
      .get(`/hyperty/user/${encodeURIComponent(message.body.resource)}/hyperty?resources=${message.body.criteria.resources.join(',')}&dataSchemes=${message.body.criteria.dataSchemes.join(',')}`)
      .reply(200, response);

    hyperty.read(message.body, true, (reply) => {
      expect(reply.code).to.equal(200);
      expect(reply.value).equal(JSON.stringify(response));
      done();
    });
  });

  it('READ - hyperties by email - advanced (none)', (done) => {
    const message = {
      "type": "read",
      "from": "hyperty://hybroker.rethink.ptinovacao.pt/ff517bb0-832e-4326-883a-4d48c5e1afae",
      "to": "domain://registry.hybroker.rethink.ptinovacao.pt/",
      "body": {
        "resource": "user://google.com/openIdTest10",
        "criteria": {
        }
      },
      "id": 5
    };

    const response = {
      "hyperty://inesc-id.pt/b7b3rs4-3245-42gn-4127-238jhdq83d8": {
        "resources" : [
          "resource1",
          "resource2"
        ],
        "dataSchemes" : [
          "ds1",
          "ds2"
        ],
        "descriptor": "hyperty-catalogue://localhost/HelloHyperty",
        "startingTime": "2016-02-08T13:42:00Z",
        "hypertyID": "hyperty://localhost/075932a5-7ef3-40dd-bcc4-34094ab907e7",
        "userID": "user://google.com/openIdTest10",
        "lastModified": "2016-02-08T13:42:53Z",
        "status": "live",
        "expires": 1211,
        "guid": "guid",
        "runtime": "runtime",
        "p2pRequester": "p2pRequester",
        "p2pHandler": "p2pHandler"
      }
    };

    const scope =
      nock(DOMAIN_URL)
      .get(`/hyperty/user/${encodeURIComponent(message.body.resource)}/hyperty`)
      .reply(200, response);

    hyperty.read(message.body, true, (reply) => {
      expect(reply.code).to.equal(200);
      expect(reply.value).equal(JSON.stringify(response));
      done();
    });
  });

  it('CREATE - hyperty', (done) => {
    const message = {
      "id":1,
      "from":"runtime://hybroker.rethink.ptinovacao.pt/9130/registry/",
      "to":"domain://registry.hybroker.rethink.ptinovacao.pt/",
      "type":"create",
      "body": {
        "idToken":null,
        "accessToken":null,
        "resource":null,
        "schema":null,
        "assertedIdentity":null,
        "value": {
          "user":"user://gmail.com/openidtest60",
          "descriptor":"hyperty-catalogue://hybroker.rethink.ptinovacao.pt/.well-known/hyperty/GroupChat",
          "url":"hyperty://hybroker.rethink.ptinovacao.pt/11111172-ff57-4b5d-a365-ccc1d5c00d70",
          "expires": 10,
          "guid": "user-guid://aaaaaaa",
          "resources":["chat"],
          "dataSchemes":["comm"],
          "status": "live",
          "runtime": "runtime://bla",
          "p2pRequester": "p2p://requester",
          "p2pHandler": "p2p://handler"
        },
        "policy":"policy",
        "auth":false
      }
};

    const response = {
      "message" : "hyperty created"
    };

    const scope =
      nock(DOMAIN_URL)
      .put(`/hyperty/user/${encodeURIComponent(message.body.value.user)}/${encodeURIComponent(message.body.value.url)}`,
        {
          'descriptor': message.body.value.descriptor,
          'expires': message.body.value.expires,
          'resources': message.body.value.resources,
          'dataSchemes': message.body.value.dataSchemes,
          'status': message.body.value.status,
          'runtime': message.body.value.runtime,
          'p2pRequester': message.body.value.p2pRequester,
          'p2pHandler': message.body.value.p2pHandler,
          'guid': message.body.value.guid
        })
      .reply(200, response);

    hyperty.create(message.body, (reply) => {
      expect(reply.code).to.equal(200);
      done();
    });
  });
});
