class User < ApplicationRecord
  rolify
  # Include default devise modules. Others available are:
  # :confirmable, :timeoutable, :omniauthable
  devise :database_authenticatable, :registerable,
         :recoverable, :rememberable, :validatable,
         :lockable, :trackable,
         :jwt_authenticatable,
         jwt_revocation_strategy: JwtDenylist

  def admin?
    has_role?(:admin)
  end

  def fullname
    "#{firstname} #{lastname}"
  end
end
