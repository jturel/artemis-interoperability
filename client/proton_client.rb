require 'json'
require 'qpid_proton'
require 'pry'

class ProtonClient < Qpid::Proton::MessagingHandler
  def initialize(url, address)
    super()
    @url = url
    @address = address
  end

  def on_container_start(container)
    c = container.connect(@url)
    c.open_receiver(@address)
  end

  def on_message(delivery, message)
    puts "MESSAGE_TYPE=#{message.properties['MESSAGE_TYPE']}"
    puts "METHOD=#{message.properties['METHOD']}"

    begin
      puts JSON.parse(message.body)
    rescue => e
      puts "No JSON body detected"
    end

    puts "\n"
  end
end

Qpid::Proton::Container.new(ProtonClient.new("amqp://localhost", "interop_test")).run
