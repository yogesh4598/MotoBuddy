<?php
require_once __DIR__ . "/../../../config/db.php";

$data = json_decode(file_get_contents("php://input"));

if (!isset($conn) || $conn === null) {
    echo json_encode(["success" => false, "message" => "Database not connected. Please start MySQL in XAMPP."]);
    exit();
}

if (!empty($data->name) && !empty($data->email) && !empty($data->password)) {
    $check = $conn->prepare("SELECT id FROM users WHERE email = :email");
    $check->bindParam(":email", $data->email);
    $check->execute();

    if ($check->rowCount() > 0) {
        http_response_code(400);
        echo json_encode(["success" => false, "message" => "Email address is already registered"]);
        exit();
    }

    $hashedPassword = password_hash($data->password, PASSWORD_BCRYPT);
    $token = bin2hex(random_bytes(32));

    $stmt = $conn->prepare("INSERT INTO users (name, email, password, token) VALUES (:name, :email, :password, :token)");
    $stmt->bindParam(":name", $data->name);
    $stmt->bindParam(":email", $data->email);
    $stmt->bindParam(":password", $hashedPassword);
    $stmt->bindParam(":token", $token);

    if ($stmt->execute()) {
        $userId = $conn->lastInsertId();
        echo json_encode([
            "success" => true,
            "message" => "User registered successfully",
            "data" => [
                "token" => $token,
                "user" => [
                    "id" => (string)$userId,
                    "name" => $data->name,
                    "email" => $data->email
                ]
            ]
        ]);
    } else {
        http_response_code(500);
        echo json_encode(["success" => false, "message" => "Failed to register user"]);
    }
} else {
    http_response_code(400);
    echo json_encode(["success" => false, "message" => "All fields (name, email, password) are required"]);
}
?>
