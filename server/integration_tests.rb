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
      expect_json_sizes(1)
      expect_json(:code => 200)
    end

    it 'should add a new hyperty' do
      put '/ruijose@inesc.pt/hyperty2', @hyperty_two_details
      expect_status(200)
      expect_json_sizes(1)
      expect_json(:code => 200)
    end
  end

  describe 'get all user hyperties' do

    it 'should return all the hyperties' do
      get '/ruijose@inesc.pt'
      expect_status(200)
      expect_json_sizes(2) # code and value
      value_hash = json_body[:value]
      expect(value_hash.keys.size).to eql(2)
      expect_json_keys("value.hyperty1", [:descriptor, :startingTime, :lastModified])
      expect_json_keys("value.hyperty2", [:descriptor, :startingTime, :lastModified])
      expect_json_types("value.hyperty1", descriptor: :string, startingTime: :string, lastModified: :string)
      expect_json_types("value.hyperty2", descriptor: :string, startingTime: :string, lastModified: :string)
      expect(json_body[:value][:hyperty1][:descriptor]).to eql("kkk11jasdasdAA")
      expect(json_body[:value][:hyperty2][:descriptor]).to eql("asdasd112AA")
      expect(json_body[:value][:hyperty1][:startingTime]).to eql(json_body[:value][:hyperty1][:lastModified])
      expect(json_body[:value][:hyperty2][:startingTime]).to eql(json_body[:value][:hyperty2][:lastModified])
    end

    it 'should return an error, user not found' do
      get '/nuno@inesc.pt'
      expect_status(404)
      expect_json_sizes(2)
      expect_json(:code => 404, :description => "User not found")
    end
  end

  describe 'update and get all hyperties' do

    it 'should update a hyperty' do
      sleep(1)
      put '/ruijose@inesc.pt/hyperty1', @hyperty_three_details
      expect_status(200)
      expect_json_sizes(1)
      expect_json(:code => 200)
    end

    it 'should get all updated hyperties' do
      get '/ruijose@inesc.pt'
      expect_status(200)
      expect_json_sizes(2) #code and value
      value_hash = json_body[:value]
      expect(value_hash.keys.size).to eql(2) #value has 2 hyperties
      expect_json_keys("value.hyperty1",  [:descriptor, :startingTime, :lastModified])
      expect_json_keys("value.hyperty2",  [:descriptor, :startingTime, :lastModified])
      expect_json_types("value.hyperty1", descriptor: :string, startingTime: :string, lastModified: :string)
      expect_json_types("value.hyperty2", descriptor: :string, startingTime: :string, lastModified: :string)
      expect(json_body[:value][:hyperty1][:descriptor]).to eql("12312321istuapt")
      expect(json_body[:value][:hyperty2][:descriptor]).to eql("asdasd112AA")
      expect(json_body[:value][:hyperty2][:startingTime]).to eql(json_body[:value][:hyperty2][:lastModified])
      expect(json_body[:value][:hyperty1][:startingTime]).to be < (json_body[:value][:hyperty1][:lastModified])
    end
  end

  describe 'a single hyperty is returned' do

    it "should return an updated hyperty" do
      get '/ruijose@inesc.pt/hyperty1'
      expect_status(200)
      expect_json_sizes(2) #http code and value
      expect_json_types(code: :integer, value: :object)
      expect_json(:code => 200)
      value_hash = json_body[:value]
      expect(value_hash.keys.size).to eql(3) #describe, lastModified and startingTime
      expect(json_body[:value][:descriptor]).to eql("12312321istuapt") #after the update from above
      expect(json_body[:value][:startingTime]).to be < (json_body[:value][:lastModified]) #after the update from above
    end

    it "should return a not updated user hyperty" do
      get '/ruijose@inesc.pt/hyperty2'
      expect_status(200)
      expect_json_sizes(2)
      expect_json_types(code: :integer, value: :object)
      expect_json(:code => 200)
      value_hash = json_body[:value]
      expect(value_hash.keys.size).to eql(3)
      expect(json_body[:value][:descriptor]).to eql("asdasd112AA")
      expect(json_body[:value][:startingTime]).to eql(json_body[:value][:lastModified]) #this hyperty was not updated . times remain equal
    end
  end

  describe 'delete user hyperty' do

    it 'should return all two hyperties' do
      get '/ruijose@inesc.pt'
      expect_status(200)
      expect_json_sizes(2)
      value_hash = json_body[:value]
      expect(value_hash.keys.size).to eql(2)
    end

    it 'should delete an user hyperty' do
      delete '/ruijose@inesc.pt/hyperty1'
      expect_status(200)
      expect_json_sizes(1)
      expect_json(:code => 200)
    end

    it 'should return all hyperties minus one' do
      get '/ruijose@inesc.pt'
      expect_status(200)
      expect_json_sizes(2)
      value_hash = json_body[:value]
      expect(value_hash.keys.size).to eql(1)
    end

    it 'should delete the hyperty' do
      delete '/ruijose@inesc.pt/hyperty2'
      expect_status(200)
      expect_json_sizes(1)
      expect_json(:code => 200)
    end

    it 'should delete an user hyperty' do
      delete '/ruijose@inesc.pt/hyperty3'
      expect_status(404)
      expect_json_sizes(2)
      expect_json(:code => 404, :description => "Data not found")
    end

    it 'should return an error, user not found' do
      delete '/ruijose1@inesc.pt/hyperty1'
      expect_status(404)
      expect_json_sizes(2)
      expect_json(:code => 404, :description => "User not found")
    end
  end
end
