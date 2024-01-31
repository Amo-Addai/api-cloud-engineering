# frozen_string_literal: true

# Ability from CanCan
class Ability
  include CanCan::Ability

  def initialize(user)
    user ||= User.new

    if user.admin?
      can :manage, :all
    else
      can :read, :all
    end
  end
end
