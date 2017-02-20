/*
* Copyright 2015-2016 INESC-ID
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/

var HttpRequest = Java.type('eu.rethink.mn.util.HttpRequest');
var client = new HttpRequest();

var JavaRequest = {

  get: function(url, callback) {
     try {
       var response = client.get(url);
       var parsedResponse = JSON.parse(response);

       print(response);
       callback(null, JSON.parse(parsedResponse.data), response.code);
     } catch(e) {
       e.printStackTrace();
     }
  },

  put: function(url, data, callback) {
     try {
       var finalData = JSON.stringify(data);
       var response = client.put(url, finalData);
       var parsedResponse = JSON.parse(response);

       print(response);
       callback(null, JSON.parse(parsedResponse.data), response.code);
     } catch(e) {
       e.printStackTrace();
     }
  },

  del: function(url, callback) {
     try {
       var response = client.del(url);
       var parsedResponse = JSON.parse(response);

       print(response);
       callback(null, JSON.parse(parsedResponse.data), response.code);
     } catch(e) {
       e.printStackTrace();
     }
  }

};

module.exports = JavaRequest;
