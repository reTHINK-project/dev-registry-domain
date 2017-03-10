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
  config.base_url = ENV["HOST"].dup << '/hyperty/dataobject'
end

describe 'domain registry api tests' do

  before {
    @data_object_details = {
      schema: "schema",
      name: "name1",
      reporter: "reporter1",
      resources: ["resource1"],
      dataSchemes: ["datascheme1"],
      status: "created",
      expires: 1000,
      runtime: "runtime",
      p2pRequester: "requester"
    }

    @data_object_two_details = {
      schema: "schema2",
      name: "name2",
      reporter: "reporter2",
      resources: ["resource2"],
      dataSchemes: ["datascheme2"],
      status: "created",
      expires: 1000,
      runtime: "runtime",
      p2pRequester: "requester"
    }

    @data_object_three_details = {
      schema: "schema3",
      name: "name3",
      reporter: "reporter1",
      resources: ["resource3"],
      dataSchemes: ["datascheme3"],
      status: "created",
      expires: 1000,
      runtime: "runtime",
      p2pRequester: "requester"
    }

    @data_object_four_details = {
      schema: "schema3",
      name: "name3",
      reporter: "reporter1",
      resources: ["resource3", "resource5"],
      dataSchemes: ["datascheme4", "datascheme3"],
      status: "created",
      expires: 1000,
      runtime: "runtime",
      p2pRequester: "requester"
    }

    @data_object_five_details = {
      schema: "schema5",
      name: "name4",
      reporter: "reporter2",
      resources: ["resource4", "resource6"],
      dataSchemes: ["datascheme4", "datascheme4"],
      status: "live",
      expires: 1
    }
  }

  describe 'create data object' do
    it 'should return a 400 bad request code' do
      # trying to register a data object without the runtime field

      invalid_data_object = {
        schema: "schema3",
        name: "name3",
        reporter: "reporter1",
        resources: ["resource3", "resource5"],
        dataSchemes: ["datascheme4", "datascheme3"],
        status: "created",
        expires: 1000,
        p2pRequester: "requester"
      }

      put '/urltest', invalid_data_object
      expect_status(400)
    end

    it 'should return a 400 bad request code' do
      # trying to register a data object with invalid fields

      invalid_data_object = {
        schema: "schema3",
        name: "name3",
        reporter: "reporter1",
        resources: ["resource3", "resource5"],
        dataSchemes: ["datascheme4", "datascheme3"],
        status: "created",
        runtime: "runtime",
        expires: 1000,
        p2pRequester: "requester",
        invalidField: "invalid"
      }

      put '/urltest', invalid_data_object
      expect_status(400)
    end

    it 'should create a new data object' do
      put '/url1', @data_object_details
      expect_status(200)
      expect_json(:message => "Data object created")
    end

    it 'should create a new data object' do
      put '/url3', @data_object_three_details
      expect_status(200)
      expect_json(:message => "Data object created")
    end

    it 'should create a second data object' do
      put '/url2', @data_object_two_details
      expect_status(200)
      expect_json(:message => "Data object created")
    end

    it 'should create a new data object' do
      put '/url4', @data_object_four_details
      expect_status(200)
      expect_json(:message => "Data object created")
    end

    it 'should create a new data object' do
      put '/url5', @data_object_five_details
      expect_status(200)
      expect_json(:message => "Data object created")
    end
  end

  describe 'get data object' do
    it 'should return a data object' do
      get '/url/url1'
      expect_status(200)
      expect_json_sizes(12)
    end

    it 'should return a data object' do
      get '/url/url2'
      expect_status(200)
      expect_json_sizes(12)
    end

    it 'should return a data not found error' do
      get '/url/url23'
      expect_status(404)
      expect_json(:message => "Not Found")
    end

    it 'should return a disconnected data object' do
      sleep(2)
      get '/url/url5'
      expect_status(200)
      expect(json_body[:status]).to eql("disconnected")
    end
  end

  describe 'get data object by hypertyReporter' do
    it 'should return a data object' do
      get '/reporter/reporter1'
      expect_status(200)
      expect_json_sizes(3)
    end

    it 'should return a data object' do
      get '/reporter/reporter2'
      expect_status(200)
      expect_json_sizes(2)
    end

    it 'should return an URL malformed error.' do
      get '/reporter/reporter2/do?rr=aa'
      expect_status(404)
      expect_json(:message => "Not Found")
    end

    it 'should return an URL malformed error.' do
      get '/reporter/reporter2/test'
      expect_status(404)
      expect_json(:message => "Not Found")
    end

    it 'should return a data not found error' do
      get '/reporter/reporter12'
      expect_status(404)
      expect_json(:message => "Not Found")
    end

    it 'should return a disconnected data object' do
      get '/reporter/reporter2'
      expect_status(200)
      expect(json_body[:url5][:status]).to eql("disconnected")
    end
  end

  describe 'get data object by name' do
    it 'should return a data object' do
      get '/name/name1'
      expect_status(200)
      expect_json_sizes(1)
    end

    it 'should return a data object' do
      get '/name/name2'
      expect_status(200)
      expect_json_sizes(1)
    end

    it 'should return a data object' do
      get '/name/name3'
      expect_status(200)
      expect_json_sizes(2)
    end

    it 'should return a data not found error' do
      get '/name/name12'
      expect_status(404)
      expect_json(:message => "Not Found")
    end

    it 'should return a disconnected data object' do
      sleep(2)
      get '/name/name4'
      expect_status(200)
      expect(json_body[:url5][:status]).to eql("disconnected")
    end
  end

  describe 'advanced search by name' do
    it 'should return 2 data objects' do
      get '/name/name3/do?resources=resource3'
      expect_status(200)
      expect_json_sizes(2)
    end

    it 'should return 2 data objects' do
      get '/name/name3/do?dataSchemes=datascheme3'
      expect_status(200)
      expect_json_sizes(2)
    end

    it 'should return 1 data objects' do
      get '/name/name2/do?resources=resource2&dataSchemes=datascheme2'
      expect_status(200)
      expect_json_sizes(1)
    end

    it 'should return 1 data objects' do
      get '/name/name3/do?resources=resource5&dataSchemes=datascheme4'
      expect_status(200)
      expect_json_sizes(1)
    end

    it 'should return not found error' do
      get '/name/name3/do?resources=resource5&dataSchemes=datascheme44'
      expect_status(404)
      expect_json(:message => "Not Found")
    end

    it 'should return 1 data objects' do
      get '/name/name3/do?resources=resource3&dataSchemes=datascheme4'
      expect_status(200)
      expect_json_sizes(1)
    end

    it 'should return 2 data objects' do
      get '/name/name3/do?resources=resource3&dataSchemes=datascheme3'
      expect_status(200)
      expect_json_sizes(2)
    end

    it 'should return 1 data objects' do
      get '/name/name1/do?resources=resource1'
      expect_status(200)
      expect_json_sizes(1)
    end

    it 'should return 1 data objects' do
      get '/name/name2/do?resources=resource2'
      expect_status(200)
      expect_json_sizes(1)
    end

    it 'should return an URL malformed error.' do
      get '/name/name3/test'
      expect_status(404)
      expect_json(:message => "Not Found")
    end

    it 'should return an URL malformed error.' do
      get '/name/name3/do?dd=dd'
      expect_status(404)
      expect_json(:message => "Not Found")
    end

    it 'should return 1 data objects' do
      get '/name/name3/do?resources=resource5'
      expect_status(200)
      expect_json_sizes(1)
    end
  end

  describe 'advanced search by reporter' do
    it 'should return 1 data objects' do
      get '/reporter/reporter1/do?resources=resource1'
      expect_status(200)
      expect_json_sizes(1)
    end

    it 'should return 1 data objects' do
      get '/reporter/reporter1/do?dataSchemes=datascheme1'
      expect_status(200)
      expect_json_sizes(1)
    end

    it 'should return 1 data objects' do
      get '/reporter/reporter1/do?dataSchemes=datascheme1&resources=resource1'
      expect_status(200)
      expect_json_sizes(1)
    end

    it 'should return 2 data objects' do
      get '/reporter/reporter1/do?dataSchemes=datascheme3&resources=resource3'
      expect_status(200)
      expect_json_sizes(2)
    end

    it 'should return 1 data objects' do
      get '/reporter/reporter1/do?dataSchemes=datascheme3&resources=resource5'
      expect_status(200)
      expect_json_sizes(1)
    end

    it 'should return 1 data objects' do
      get '/reporter/reporter2/do?dataSchemes=datascheme2&resources=resource2'
      expect_status(200)
      expect_json_sizes(1)
    end

    it 'should return 1 data objects' do
      get '/reporter/reporter1/do?dataSchemes=datascheme4&resources=resource5'
      expect_status(200)
      expect_json_sizes(1)
    end

    it 'should return 1 data objects' do
      get '/reporter/reporter2/do?resources=resource2'
      expect_status(200)
      expect_json_sizes(1)
    end

    it 'should return 1 data objects' do
      get '/reporter/reporter1/do?resources=resource5'
      expect_status(200)
      expect_json_sizes(1)
    end

    it 'should return 2 data objects' do
      get '/reporter/reporter1/do?resources=resource3'
      expect_status(200)
      expect_json_sizes(2)
    end

    it 'should return a data not found error' do
      get '/reporter/reporter1/do?resources=resource12'
      expect_status(404)
      expect_json(:message => "Not Found")
    end

    it 'should return 1 data objects' do
      get '/name/name3/do?resources=resource5'
      expect_status(200)
      expect_json_sizes(1)
    end
  end
  #
  # describe 'delete data objects' do
  #   it 'sould delete a data object' do
  #     delete '/url/url1'
  #     expect_status(200)
  #     expect_json(:message => "Data object deleted")
  #   end
  #
  #   it 'sould delete a data object' do
  #     delete '/url/url2'
  #     expect_status(200)
  #     expect_json(:message => "Data object deleted")
  #   end
  #
  #   it 'sould delete a data object' do
  #     delete '/url/url3'
  #     expect_status(200)
  #     expect_json(:message => "Data object deleted")
  #   end
  #
  #   it 'sould delete a data object' do
  #     delete '/url/url4'
  #     expect_status(200)
  #     expect_json(:message => "Data object deleted")
  #   end
  #
  #   it 'sould return a data not found error' do
  #     delete '/url/url234'
  #     expect_status(404)
  #     expect_json(:message => "Not Found")
  #   end
  # end
end
