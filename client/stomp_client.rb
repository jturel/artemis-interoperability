require 'json'
require 'stomp'

config = {
  hosts: [
    {
      host: 'localhost',
      port: '61613',
    }
  ],
  connect_headers: {
    'accept-version': '1.2',
    'host': 'localhost',
    'heart-beat': '30000,0',
  }
}

client = Stomp::Client.new(config)

options = {
  #'selector' => "METHOD = 'writeNullableSimpleString'"
}

client.subscribe('interop_test', options) do |message|
  puts "CONTENT_LENGTH=#{message.headers['content-length']}"
  puts "MESSAGE_TYPE=#{message.headers['MESSAGE_TYPE']}"
  puts "METHOD=#{message.headers['METHOD']}"

  begin
    parsed = JSON.parse(message.body)
    if parsed
      puts parsed
    end
  rescue
    puts "No JSON body detected"
  end

  puts "\n"
end

at_exit do
  client.close
end

sleep
