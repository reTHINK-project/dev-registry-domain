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
      url: "url",
      reporter: "reporter"
    }

    @data_object_two_details = {
      schema: "schema2",
      url: "url2",
      reporter: "reporter2"
    }

    @data_object_three_details = {
      schema: "schema3",
      url: "url3",
      reporter: "reporter3"
    }
  }

  describe 'create data object' do
    it 'should create a new data object' do
      put '/mychat', @data_object_details
      expect_status(200)
      expect_json(:message => "Data object created")
    end

    it 'should create a second data object' do
      put '/mychat2', @data_object_two_details
      expect_status(200)
      expect_json(:message => "Data object created")
    end
  end

  describe 'get data object' do
    it 'should return a data object' do
      get '/mychat'
      expect_status(200)
      expect_json_sizes(6)
    end

    it 'should return a data object' do
      get '/mychat2'
      expect_status(200)
      expect_json_sizes(6)
    end

    it 'should return a data not found error' do
      get '/mychat23'
      expect_status(404)
      expect_json(:message => "Data not found")
    end
  end

  describe 'delete data objects' do
    it 'sould delete a data object' do
      delete '/mychat'
      expect_status(200)
      expect_json(:message => "Data object deleted")
    end

    it 'sould return a data not found error' do
      delete '/mychat'
      expect_status(404)
      expect_json(:message => "Data not found")
    end
  end
end
