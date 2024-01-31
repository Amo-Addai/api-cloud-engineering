class Item < ApplicationRecord
  include Sluggable
  friendly_id :slug_candidates, use: :slugged

  has_and_belongs_to_many :categories

  has_one :address, dependent: :destroy
  accepts_nested_attributes_for(:address,
                                allow_destroy: true,
                                reject_if: proc { |attributes|
                                  attributes['street1'].blank? &&
                                    attributes['street2'].blank? &&
                                    attributes['city'].blank? && attributes['state'].blank? &&
                                    attributes['zip'].blank?
                                })

  enum price_quartile: %i[zero one two three four]
  enum cuisine: %i[afghani african american asian bakery bar_lounge barbeque belgian brazilian british burger california canadian caribbean chinese comfort_food contemporary continental creo_cajun cuban deli diner donuts eclectic_american ethiopian eurasian european french fusion gastro_pub german global greek hamburger ice_cream indian international_sandwich irish italian jamaican japanese juice_smoothies kebab korean latin mediterranean mexican mexican_southwestern middle_eastern modern new_american nordic north_african organic pakistani peruvian pizza portuguese puerto_rican ramen russian salads salvadoran seafood serbian south_american southern spanish steak sushi tapas_small_plates thai turkish vegan_vegetarian vietnamese]

  def formatted_start_date
    start_date&.strftime('%m/%d/%y')
  end

  def formatted_end_date
    end_date&.strftime('%m/%d/%y')
  end

  def formatted_custom_dates
    custom_dates&.map { |date| date.strftime('%m/%d/%y') }
  end

  def formatted_start_date=(val)
    self.start_date = val.blank? ? nil : Date.strptime(val, '%m/%d/%y')
  end

  def formatted_end_date=(val)
    self.end_date = val.blank? ? nil : Date.strptime(val, '%m/%d/%y')
  end

  def formatted_custom_dates=(val)
    self.custom_dates = val.blank? ? [] : val.map { |date| Date.strptime(date, '%m/%d/%y') }.sort
  end

  private
    def slug_candidates
      [:name, %i[name id]]
    end
end
