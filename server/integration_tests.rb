require 'airborne'

Airborne.configure do |config|
  config.base_url = 'http://localhost:4567/hyperty/user'
end

describe 'domain registry api tests' do

  before {
    @hyperty_details = {
      descriptor: "kkk11jasdasdAA"
    }

    @hyperty_two_details = {
      descriptor: "asdasd112AA"
    }

    @hyperty_three_details = {
      descriptor: "12312321istuapt"
    }
  }

  describe 'create user hyperty' do

    it 'should add a new hyperty' do
      put '/ruijose@inesc.pt/hyperty1', @hyperty_details
      expect_status(200)
      expect_json(:message => "hyperty created")
    end

    it 'should add a new hyperty' do
      put '/ruijose@inesc.pt/hyperty2', @hyperty_two_details
      expect_status(200)
      expect_json(:message => "hyperty created")
    end
  end

  describe 'get all user hyperties' do

    it 'should return all the hyperties' do
      get '/ruijose@inesc.pt'
      expect_status(200)
      expect_json_sizes(2)
      expect_json_keys("hyperty1", [:descriptor, :startingTime, :lastModified])
      expect_json_keys("hyperty2", [:descriptor, :startingTime, :lastModified])
      expect_json_types("hyperty1", descriptor: :string, startingTime: :string, lastModified: :string)
      expect_json_types("hyperty2", descriptor: :string, startingTime: :string, lastModified: :string)
      expect(json_body[:hyperty1][:descriptor]).to eql("kkk11jasdasdAA")
      expect(json_body[:hyperty2][:descriptor]).to eql("asdasd112AA")
      expect(json_body[:hyperty1][:startingTime]).to eql(json_body[:hyperty1][:lastModified])
      expect(json_body[:hyperty2][:startingTime]).to eql(json_body[:hyperty2][:lastModified])
    end

    it 'should return an error, user not found' do
      get '/nuno@inesc.pt'
      expect_status(400)
      expect_json(:message => "user not found")
    end
  end

  describe 'update and get all hyperties' do

    it 'should update a hyperty' do
      sleep(1)
      put '/ruijose@inesc.pt/hyperty1', @hyperty_three_details
      expect_status(200)
      expect_json(:message => "hyperty created")
    end

    it 'should get all updated hyperties' do
      get '/ruijose@inesc.pt'
      p json_body
      expect_status(200)
      expect_json_sizes(2)
      expect_json_keys("hyperty1", [:descriptor, :startingTime, :lastModified])
      expect_json_keys("hyperty2", [:descriptor, :startingTime, :lastModified])
      expect_json_types("hyperty1", descriptor: :string, startingTime: :string, lastModified: :string)
      expect_json_types("hyperty2", descriptor: :string, startingTime: :string, lastModified: :string)
      expect(json_body[:hyperty1][:descriptor]).to eql("12312321istuapt")
      expect(json_body[:hyperty2][:descriptor]).to eql("asdasd112AA")
      expect(json_body[:hyperty2][:startingTime]).to eql(json_body[:hyperty2][:lastModified])
      expect(json_body[:hyperty1][:startingTime]).to be < (json_body[:hyperty1][:lastModified])
    end
  end

  describe 'delete user hyperty' do

    it 'should delete an user hyperty' do
      delete '/ruijose@inesc.pt/hyperty1'
      expect_status(200)
      expect_json(:message => "hyperty deleted")
    end

    it 'should delete the hyperty' do
      delete '/ruijose@inesc.pt/hyperty2'
      expect_status(200)
      expect_json(:message => "hyperty deleted")
    end

    it 'should delete an user hyperty' do
      delete '/ruijose@inesc.pt/hyperty3'
      expect_status(400)
      expect_json(:message => "data not found")
    end

    it 'should return an error, user not found' do
      delete '/ruijose1@inesc.pt/hyperty1'
      expect_status(400)
      expect_json(:message => "user not found")
    end
  end
end
