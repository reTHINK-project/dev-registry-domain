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
    @hyperty_details = {
      resources: ["chat", "voice"],
      dataSchemes: ["comm"],
      descriptor: "descriptor1",
      expires: 1200,
      status: "created"
    }
  }

  describe 'create user hyperty' do

    it 'should add a new hyperty' do #non-existent user creates non-existent hyperty
      put '/hyperty/user/ruijose@inesc-id/hyperty_keep_alive', @hyperty_details
      expect_status(200)
      expect_json(:message => "Hyperty created")
    end
  end

  describe 'it should update hyperty fields' do

    it 'should get hyperty fields' do
      get '/hyperty/user/ruijose@inesc-id'
      expect_status(200)
      expect_json_keys("hyperty_keep_alive", [:descriptor, :startingTime, :lastModified, :expires, :resources, :dataSchemes, :status])
      expect(json_body[:hyperty_keep_alive][:descriptor]).to eql("descriptor1")
      expect(json_body[:hyperty_keep_alive][:resources]).to eql(["chat", "voice"])
      expect(json_body[:hyperty_keep_alive][:dataSchemes]).to eql(["comm"])
      expect(json_body[:hyperty_keep_alive][:expires]).to eql(1200)
      expect(json_body[:hyperty_keep_alive][:status]).to eql("created")
    end

    it 'should update expires and descriptor field' do

      new_fields = {
        expires: 3000,
        descriptor: "new_descriptor"
      }

      put '/hyperty/url/hyperty_keep_alive', new_fields
      expect_status(200)
      expect_json(:message => "Hyperty updated")
    end

    it 'should get new hyperty fields' do
      get '/hyperty/user/ruijose@inesc-id'
      expect_status(200)
      expect_json_keys("hyperty_keep_alive", [:descriptor, :startingTime, :lastModified, :expires, :resources, :dataSchemes])
      expect(json_body[:hyperty_keep_alive][:descriptor]).to eql("new_descriptor")
      expect(json_body[:hyperty_keep_alive][:resources]).to eql(["chat", "voice"])
      expect(json_body[:hyperty_keep_alive][:dataSchemes]).to eql(["comm"])
      expect(json_body[:hyperty_keep_alive][:expires]).to eql(3000)
    end

    it 'should perform a keep alive' do

      new_fields = {}

      put '/hyperty/url/hyperty_keep_alive', new_fields
      expect_status(200)
      expect_json(:message => "Keep alive")
    end

    it 'should get new hyperty fields' do
      get '/hyperty/user/ruijose@inesc-id'
      expect_status(200)
      expect_json_keys("hyperty_keep_alive", [:descriptor, :startingTime, :lastModified, :expires, :resources, :dataSchemes])
      expect(json_body[:hyperty_keep_alive][:descriptor]).to eql("new_descriptor")
      expect(json_body[:hyperty_keep_alive][:resources]).to eql(["chat", "voice"])
      expect(json_body[:hyperty_keep_alive][:dataSchemes]).to eql(["comm"])
      expect(json_body[:hyperty_keep_alive][:expires]).to eql(3000)
    end

    it 'should try to update an non existant hyperty' do
      new_fields = {}

      put '/hyperty/url/hyperty_keep_alive_error', new_fields
      expect_status(404)
      expect_json(:message => "Not Found")
    end
  end
end
