# Copyright 2015-2016 inesc-id-ID
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

require 'airborne'

Airborne.configure do |config|
  config.base_url = ENV["HOST"].dup
end

describe 'domain registry api tests' do

  before {
    @data_object_details = {
      resources: ["chat", "voice"],
      dataSchemes: ["comm"],
      name: "name",
      schema: "schema",
      reporter: "reporter",
      status: "live",
      expires: 1000,
      runtime: "runtime",
      p2pRequester: "requester"
    }
  }

  describe 'create a data object' do

    it 'should add a new data object' do
      put '/hyperty/dataobject/url_test', @data_object_details
      expect_status(200)
      expect_json(:message => "Data object created")
    end
  end

  describe 'it should update data objects fields' do

    it 'should get data objects fields' do
      get '/hyperty/dataobject/url/url_test'
      expect_status(200)
      expect_json_keys([:name, :startingTime, :lastModified, :reporter, :resources, :dataSchemes, :url, :schema, :status, :expires, :runtime, :p2pRequester])
      expect(json_body[:resources]).to eql(["chat", "voice"])
      expect(json_body[:dataSchemes]).to eql(["comm"])
      expect(json_body[:name]).to eql("name")
      expect(json_body[:reporter]).to eql("reporter")
      expect(json_body[:url]).to eql("url_test")
      expect(json_body[:schema]).to eql("schema")
      expect(json_body[:status]).to eql("live")
      expect(json_body[:expires]).to eql(1000)
      expect(json_body[:runtime]).to eql("runtime")
      expect(json_body[:p2pRequester]).to eql("requester")
    end

    it 'should update url field' do

      new_fields = {
        schema: "new_schema"
      }

      put '/dataobject/url/url_test', new_fields
      expect_status(200)
      expect_json(:message => "data object updated")
    end

    it 'should get data objects fields' do
      get '/hyperty/dataobject/url/url_test'
      expect_status(200)
      expect_json_keys([:name, :startingTime, :lastModified, :reporter, :resources, :dataSchemes, :url, :schema, :status, :expires, :runtime, :p2pRequester])
      expect(json_body[:resources]).to eql(["chat", "voice"])
      expect(json_body[:dataSchemes]).to eql(["comm"])
      expect(json_body[:name]).to eql("name")
      expect(json_body[:reporter]).to eql("reporter")
      expect(json_body[:url]).to eql("url_test")
      expect(json_body[:schema]).to eql("new_schema")
      expect(json_body[:status]).to eql("live")
      expect(json_body[:expires]).to eql(1000)
      expect(json_body[:runtime]).to eql("runtime")
      expect(json_body[:p2pRequester]).to eql("requester")
    end

    it 'should perform a keep alive' do

      new_fields = {}

      put '/dataobject/url/url_test', new_fields
      expect_status(200)
      expect_json(:message => "Keep alive")
    end

    it 'should get data objects fields' do
      get '/hyperty/dataobject/url/url_test'
      expect_status(200)
      expect_json_keys([:name, :startingTime, :lastModified, :reporter, :resources, :dataSchemes, :url, :schema, :status, :expires, :runtime, :p2pRequester])
      expect(json_body[:resources]).to eql(["chat", "voice"])
      expect(json_body[:dataSchemes]).to eql(["comm"])
      expect(json_body[:name]).to eql("name")
      expect(json_body[:reporter]).to eql("reporter")
      expect(json_body[:url]).to eql("url_test")
      expect(json_body[:schema]).to eql("new_schema")
      expect(json_body[:status]).to eql("live")
      expect(json_body[:expires]).to eql(1000)
      expect(json_body[:runtime]).to eql("runtime")
      expect(json_body[:p2pRequester]).to eql("requester")
    end

    it 'should try to update an non existant data objects' do
      new_fields = {}

      put '/dataobject/url/hyperty_keep_alive_error', new_fields
      expect_status(404)
      expect_json(:message => "Not Found")
    end
  end
end
