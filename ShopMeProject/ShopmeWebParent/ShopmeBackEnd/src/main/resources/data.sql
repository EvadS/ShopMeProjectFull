-- INSERT INTO `shopmedb`.`roles` (`name`,`description`) VALUES ('Admin', 'manage everything');
-- INSERT INTO `shopmedb`.`roles` (`name`,`description`) VALUES ('Editor', 'manage categories, brands, products, articles and menus');
-- INSERT INTO `shopmedb`.`roles` (`name`,`description`) VALUES ('Shipper', 'view products, view orders and update order status');
-- INSERT INTO `shopmedb`.`roles` (`name`,`description`) VALUES ('Salesperson', 'manage product price, customers, shipping and sales report');
-- INSERT INTO `shopmedb`.`roles` (`name`,`description`) VALUES ('Assistant', 'manage questions and reviews');
--
-- INSERT INTO `shopmedb`.`users` (`email`,  `first_name`, `last_name`, `password`) VALUES ('admin@mail.com',  'evheniy', 'skyba', '$2a$10$SG2kNxJtqxhqUqsoKbyEJeRinZ/izyZ2mTH8EGT.rKpkWnfl8nuuK');
--
-- INSERT INTO `shopmedb`.`users_roles` (`user_id`, `role_id`) VALUES ('1', '1');

/*
 INSERT INTO `shopmedb`.`categories` (`alias`, `enabled`, `image`, `name`)
 VALUES ('alias',  b'0', 'image path', 'name');
 */