FROM ruby:2.2

RUN gem install rspec
RUN gem install airborne

WORKDIR /specs

ADD hyperties_spec.rb /specs/hyperties_spec.rb
ADD data_objects_spec.rb /specs/data_objects_spec.rb

CMD HOST=${SERVER}:${PORT} rspec hyperties_spec.rb && HOST=${SERVER}:${PORT} rspec data_objects_spec.rb
