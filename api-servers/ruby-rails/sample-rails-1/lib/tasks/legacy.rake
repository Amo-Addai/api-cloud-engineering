require 'faraday'
require 'json'

namespace :legacy do
  desc 'Sync legacy system with this'
  task sync: :environment do
    Item.destroy_all
    Category.destroy_all

    header = {'Accept' => 'application/json'}
    base_url = ENV['LEGACY_URL']

    get_legacy_category = lambda do |id=nil|
      url = id.nil? ? "#{base_url}/categories.json" : "#{base_url}/categories/#{id}.json"
      return JSON.parse(Faraday.get(url, header).body, symbolize_names: true)
    end

    get_legacy_items_for_category = lambda do |category_id|
      url = "#{base_url}/items.json"
      return JSON.parse(Faraday.get(url, {bookmark_type_id: category_id}, header).body, symbolize_names: true)
    end

    map_to_category = lambda do |legacy_category, parent=nil|
      category = Category.create!(
        name: legacy_category[:name],
        image_url: legacy_category[:url],
        hidden: legacy_category[:hidden],
        position: legacy_category[:position],
        slug: legacy_category[:slug],
        description: legacy_category[:description],
        subtitle: legacy_category[:subtitle]
      )
      unless parent.nil?
        category.parent = parent
        category.save!
      end
      return category
    end

    map_to_item = lambda do |legacy_item, category|
      cuisine_lookup = {60=>"afghani", 2=>"african", 1=>"american", 3=>"asian", 66=>"bakery", 5=>"barbeque", 4=>"bar_lounge", 6=>"belgian", 7=>"brazilian", 8=>"british", 70=>"burger", 9=>"california", 65=>"canadian", 10=>"caribbean", 11=>"chinese", 12=>"comfort_food", 53=>"contemporary", 13=>"continental", 14=>"creo_cajun", 58=>"cuban", 15=>"deli", 64=>"diner", 69=>"donuts", 17=>"eclectic_american", 18=>"ethiopian", 67=>"eurasian", 16=>"european", 19=>"french", 20=>"fusion", 21=>"gastro_pub", 22=>"german", 75=>"global", 23=>"greek", 24=>"hamburger", 73=>"ice_cream", 25=>"indian", 74=>"international_sandwich", 63=>"irish", 26=>"italian", 27=>"jamaican", 28=>"japanese", 68=>"juice_smoothies", 40=>"kebab", 29=>"korean", 33=>"latin", 30=>"mediterranean", 31=>"mexican", 34=>"mexican_southwestern", 35=>"middle_eastern", 55=>"modern", 54=>"new", 37=>"nordic", 36=>"north_african", 38=>"organic", 39=>"pakistani", 52=>"peruvian", 56=>"pizza", 41=>"portuguese", 48=>"puerto_rican", 57=>"ramen", 49=>"russian", 71=>"salads", 59=>"salvadoran", 50=>"seafood", 61=>"serbian", 43=>"south_american", 32=>"southern", 42=>"spanish", 44=>"steak", 45=>"sushi", 46=>"tapas_small_plates", 47=>"thai", 62=>"turkish", 51=>"vegan_vegetarian", 72=>"vietnamese"}
      item = Item.friendly_find(legacy_item[:slug])
      if item.nil?
        item = Item.create!(
          name: legacy_item[:title],
          image_url: legacy_item[:url],
          description: legacy_item[:body],
          website_url: legacy_item[:website],
          reservation_url: legacy_item[:reservation_url],
          book_url: legacy_item[:book_url],
          menu_url: legacy_item[:menu_url],
          phone: legacy_item[:phone],
          venue_name: legacy_item[:venue_name],
          start_date: legacy_item[:start_date],
          end_date: legacy_item[:end_date],
          special_notes: legacy_item[:special_notes],
          custom_dates: legacy_item[:formatted_custom_dates],
          slug: legacy_item[:slug],
          price_quartile: legacy_item[:price_quartile],
          cuisine: cuisine_lookup[legacy_item[:cuisine_id]]
        )
        address = legacy_item[:address]
        unless address.nil?
          item.create_address(
            street1: address[:street1],
            street2: address[:street2],
            city: address[:city],
            state: address[:state],
            zip: address[:zip],
            country_code: address[:country_code]
          )
        end
      end
      category.items << item
      item.save!
      category.save!
      return item
    end

    parent_categories = get_legacy_category.call()
    parent_categories.each do |parent_category|
      full_parent_category = get_legacy_category.call(parent_category[:id])
      new_parent_category = map_to_category.call(full_parent_category)

      full_parent_category[:subtypes].each do |subcategory|
        full_subcategory_category = get_legacy_category.call(subcategory[:id])
        new_subcategory = map_to_category.call(full_subcategory_category, new_parent_category)

        get_legacy_items_for_category.call(subcategory[:id]).each do |item|
          # no need to fetch full object, just map
          puts "saving item: #{item[:id]}"
          new_item = map_to_item.call(item, new_subcategory)
          puts "saved item: #{new_item.id}, category: #{new_subcategory.id}"
        end
      end
    end
  end
end
