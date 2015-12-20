require 'airborne'

Airborne.configure do |config|
  config.base_url = 'http://localhost:4567/hyperty/user'
end

describe 'domain registry api tests' do

  before {
    @hyperty_details = {
      descriptor: "kkk11jasdasdAA",
    }

    @hyperty_two_details = {
      descriptor: "asdasd112AA",
    }
  }

  describe 'create user hyperty' do

    it 'should add a new hyperty' do
      put '/user%3A%2F%2Fua.pt%2F123/hyperty%3A%2F%2Fua.pt%2Fb7b3rs4-3245-42gn-4327-238jhdq83d8', @hyperty_details
      expect_status(200)
      expect_json(:message => "hyperty created") 
    end

    it 'should add a new hyperty' do
      put '/user%3A%2F%2Fua.pt%2F123/hyperty%3A%2F%2Fua.pt%2Fb7H3rs4-3245-42gf-4027-138aadq23d8', @hyperty_two_details
      expect_status(200)
      expect_json(:message => "hyperty created")
    end
  end

  describe 'get all user hyperties' do

    all_hyperties = {
      "hyperty%3A%2F%2Fua.pt%2Fb7b3rs4-3245-42gn-4327-238jhdq83d8"   => @hyperty_details,
      "hyperty%3A%2F%2Fua.pt%2Fb7H3rs4-3245-42gf-4027-138aadq23d8"   => @hyperty_two_details
    }

    it 'should return all the hyperties' do
      get '/user%3A%2F%2Fua.pt%2F123'
      expect_status(200)
      expect_json(all_hyperties)
    end

    it 'should return an error, user not found' do
      get '/user%3A%2F%2Fua.pt%2F124'
      expect_status(400)
      expect_json(:message => "user not found")
    end
  end

  describe 'delete user hyperty' do

    it 'should delete an user hyperty' do
      delete '/user%3A%2F%2Fua.pt%2F123/hyperty%3A%2F%2Fua.pt%2Fb7b3rs4-3245-42gn-4327-238jhdq83d8'
      expect_status(200)
      expect_json(:message => "hyperty deleted")
    end

    it 'should delete the hyperty' do
      delete '/user%3A%2F%2Fua.pt%2F123/hyperty%3A%2F%2Fua.pt%2Fb7H3rs4-3245-42gf-4027-138aadq23d8'
      expect_status(200)
      expect_json(:message => "hyperty deleted")
    end

    it 'should delete an user hyperty' do
      delete '/user%3A%2F%2Fua.pt%2F123/hyperty%3A%2F%2Fua.pt%2Fb7b3rs4-3245-42gn-4327-238jhdq83d8'
      expect_status(400)
      expect_json(:message => "data not found")
    end

    it 'should return an error, data not found' do
      delete '/user%3A%2F%2Fua.pt%2F123/hyperty%3A%2F%2Fua.pt%2Fb7H3rs4-3245-42gf-4027-138aadq23e8'
      expect_status(400)
      expect_json(:message => "data not found")
    end

    it 'should return an error, user not found' do
      delete '/user%3A%2F%2Foa.pt%2F123/hyperty%3A%2F%2Fua.pt%2Fb7H3rs4-3245-42gf-4027-138aadq23d8'
      expect_status(400)
      expect_json(:message => "user not found")
    end
  end
end
